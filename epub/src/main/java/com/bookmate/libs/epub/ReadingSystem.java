package com.bookmate.libs.epub;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


/**
 * Created by khmelev on 29.04.14.
 * provides epub navigation (stores current item, progress); generates cfi, bookmark; loads epub items
 * item is a single file from epub archive (<spine> element contains list of items).
 */
public class ReadingSystem {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = "reader " + ReadingSystem.class.getSimpleName();
    private static final String BOOKMARK_REGEX = "(?:d=(?:[^&]+)&)?i=([^:]+)(?::([\\d\\.]+))?";
    private static final Pattern BOOKMARK_PATTERN = Pattern.compile(BOOKMARK_REGEX);
    private static final Pattern CFI_PATTERN = Pattern.compile("(\\d+)!");
    private static final String EPUB_SCHEME = "epub://root/";

    /**
     * should have a synchronized access, so use {@link #getEpubFile}
     */
    private EpubFile epubFile;

    private final History history;
    private EpubItems epubItems;
    private Metadata metadata;
    private Listener listener;

    /**
     * can be from 0 .. 1
     */
    private float progressInItem = 0;

    private int currentItemIndex = 0;
    @Nullable
    private NavPoint[] chapters;

    public ReadingSystem(EpubFile file) throws XPathExpressionException {
        Log.d(LOG_TAG, "ReadingSystem()");
        epubFile = file;
        epubItems = new EpubItems(epubFile);
        metadata = new Metadata(epubFile);
        history = new History(this);
    }

    public History history() {
        return history;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private synchronized EpubFile getEpubFile() {
        return epubFile;
    }

    /**
     * Useful when book was fully cached offline and can be read without network
     */
    public synchronized void swapEpubFile(EpubFile file) {
        epubFile = file;
    }

    public int getCurrentItemIndex() {
        return currentItemIndex;
    }

    //// navigating methods

    public boolean isLastPageInBook(int pageIndex, int pagesCount) {
        return !validItemIndex(currentItemIndex + 1) && pageIndex == pagesCount - 1;
    }

    /**
     * Tunes the progress to the progress of first character on the page
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean adjustProgressToPage(int pageIndex, int pageCount) {
        return goTo(currentItemIndex, (float) pageIndex / pageCount, false);
    }

    /**
     * goes to next/previous page and loads next item if needed.
     */
    public void goToPage(int currentPageIndex, int pagesCount, boolean nextPage) {

        int newPageIndex = currentPageIndex + (nextPage ? 1 : -1);
        boolean goSuccesseful;

        if (newPageIndex >= pagesCount) {
            goSuccesseful = goTo(currentItemIndex + 1, 0);
        } else
            goSuccesseful = newPageIndex < 0 ? goTo(currentItemIndex - 1, 1) : goToPageInThisItem(newPageIndex, pagesCount);

        if (!goSuccesseful && nextPage && listener != null)
            listener.onEndOfBook();
    }

    public boolean goToPageInThisItem(int pageIndex, int pagesCount) {
        return goTo(currentItemIndex, (float) pageIndex / pagesCount);
    }

    /**
     * @param bookProgress 0 .. 100
     */
    public void goToBookProgress(float bookProgress) {
        long wantedOffset = bookProgress2Offset(bookProgress);
        final int itemIndex = epubItems.getIndex(wantedOffset);
        final EpubItem epubItem = epubItems.get(itemIndex);

        goTo(itemIndex, ((float) (wantedOffset - epubItem.getSizeUpToThisItem())) / epubItem.getSize());
    }

    /**
     * Parse and restore progress by bookmark with format like "d=vgElOTYR&i=RoW6SOwD:0"
     *
     * @return true if progress can be restored, false otherwise
     */
    @SuppressWarnings({"UnusedReturnValue", "BooleanMethodIsAlwaysInverted"})
    public boolean goToBookmark(String bookmark) {
        if (!TextUtils.isEmpty(bookmark)) {
            Matcher matcher = BOOKMARK_PATTERN.matcher(bookmark);

            if (matcher.find()) {
                String itemId = matcher.group(1);
                String progressString = matcher.group(2);
                float progress = TextUtils.isEmpty(progressString) ? 0 : Float.valueOf(progressString);
                if (goTo(itemId, progress / 100)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Move to the item specified by id at specific progress
     */
    private boolean goTo(String id, float progress) {
        return goTo(epubItems.getIndex(id), progress);
    }

    /**
     * Move to the item specified by index in navigation list at specific progress
     */
    public boolean goTo(int itemIndex, float progress) {
        return goTo(itemIndex, progress, true);
    }

    /**
     * Move to the item specified by index in navigation list at specific progress
     */
    private boolean goTo(int itemIndex, float progress, boolean notify) {
        if (!validItemIndex(itemIndex))
            return false;

        currentItemIndex = itemIndex;
        progressInItem = clampProgress(progress); // Progress should always be between 0 and 1
        if (notify)
            listener.onProgressChanged();
        return true;
    }

    private float clampProgress(float progress) {
        return Math.min(1, Math.max(0, progress));
    }

    //// progress and read size calculation

    /**
     * tries to estimate how much text symbols the user has read.
     * we can't just return bookProgress * size, the size can be really big because of images, markup etc. so we try to perform some carefull calculation instead.
     */
    public int alreadyReadSize() {
        return (int) (epubItems.sumTextLengths(currentItemIndex) + epubItems.get(getCurrentItemIndex()).getTextLength() * progressInItem());
    }

    /**
     * @return 0 .. 1
     */
    public float progressInItem() {
        return progressInItem;
    }

    /**
     * used for paywall. Returns progress up to the beginning of current page (will be 0.5 on the page 2 of 2 in a single item book).
     */
    public float bookProgress() {
        return calcBookProgress(progressInItem);
    }

    /**
     * USE THIS to calculate progress for displaying to user: bookProgress returns < 100% on last page and bookProgressPage returns 100% on last page.
     * consider a book of 1 item of 2 pages. On page 2 of 2 mNavProgress would still be 0.5 (end of page 1) => progress would be 0.5 and the progress bar would be in the middle, but it's confusing to the user, because it's the last page. Hence we need a different method.
     */
    public float bookProgressForDisplay(int page, int pageCount) {
        return calcBookProgress(pageCount == 1 ? 1 : ((float) page) / (pageCount - 1)); // now on last page in item the progress will be 1
    }

    /**
     * https://github.com/bookmate/bookmate-api4/wiki/%D0%A0%D0%B0%D1%81%D1%87%D0%B5%D1%82-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B5%D1%81%D1%81%D0%B0-%D1%87%D1%82%D0%B5%D0%BD%D0%B8%D1%8F#%D0%A0%D0%B0%D1%81%D1%87%D0%B5%D1%82-%D0%BE%D0%B1%D1%89%D0%B5%D0%B3%D0%BE-%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B5%D1%81%D1%81%D0%B0-%D0%BF%D0%BE-%D0%BA%D0%BD%D0%B8%D0%B3%D0%B5-%D0%BA%D0%BE%D1%82%D0%BE%D1%80%D1%8B%D0%B9-%D0%BE%D1%82%D1%81%D1%8B%D0%BB%D0%B0%D0%B5%D1%82%D1%81%D1%8F-%D0%BD%D0%B0-%D1%81%D0%B5%D1%80%D0%B2%D0%B5%D1%80-%D0%B8-%D0%B2%D0%B8%D0%B4%D0%B5%D0%BD-%D0%BD%D0%B0-%D1%81%D0%B0%D0%B9%D1%82%D0%B5-%D1%83-%D0%BA%D0%BD%D0%B8%D0%B3
     * <p/>
     * progress = ( item_size * ( item_progress / 100 ) + current ) * 100 / whole;
     * <p/>
     * item_size — размер текущего файла в байтах
     * item_progress — текущий прогресс по файлу, расчет описан ниже
     * current — размер в байтах всех файлов по порядку до текущего (не включая текущий)
     * whole — размер в байтах всех файлов
     */
    private float calcBookProgress(float itemProgress) {
        return (epubItems.get(currentItemIndex).getSize() * itemProgress + epubItems.sumSizes(currentItemIndex)) * 100f / epubItems.getTotalSize();
    }

    private long bookProgress2Offset(float bookProgress) {
        return (long) (clampProgress(bookProgress / 100.f) * epubItems.getTotalSize());
    }

    //// Items indexes, ids, paths etc

    public boolean validItemIndex(int itemIndex) {
        return itemIndex >= 0 && itemIndex < epubItems.getItemsCount();
    }

    public boolean isLastPage(int pageIndex, int pageCount) {
        return getCurrentItemIndex() == epubItems.getItemsCount() - 1 && pageIndex == pageCount - 1;
    }

//    /**
//     * used when user clicked a link
//     *
//     * @param url epub://root/blah.html#x63cf
//     * @return item index in the items array.
//     */
//    public int getItemIndex(String url) {
//        String path = url.replace(EPUB_SCHEME, "");
//        int itemPathSymbolCount = path.indexOf("#");
//        if (itemPathSymbolCount >= 0) // link contained an #
//            path = path.substring(0, itemPathSymbolCount);
//
//        EpubItem epubItem = epubItems.getByPath(path);
//        return epubItems.getIndex(epubItem.getId());
//    }

    /**
     * @return id is described in OPF manifest (like bookItem_18)
     */
    private String getItemId(int itemIndex) {
        return epubItems.get(itemIndex).getId();
    }

    /**
     * @return id is described in OPF manifest (like bookItem_18)
     */
    public String getCurrentItemId() {
        return getItemId(currentItemIndex);
    }

    /**
     * @return current item path from OPF manifest (bla.html)
     */
    public String getCurrentItemPath() {
        return getItemPath(currentItemIndex);
    }

    /**
     * @return item path from OPF manifest (bla.html)
     */
    public String getItemPath(int itemIndex) {
        if (!validItemIndex(itemIndex))
            throw new IllegalArgumentException("wrong index: " + itemIndex + " items count: " + epubItems.getItemsCount());
        return epubItems.get(itemIndex).getPath();
    }

    /**
     * @param cfi pExample cfi /6/2!/,/2/4/6/3:211,/2/4/6/3:219
     * @return item path from OPF manifest (bla.html)
     */
    public String getItemPath(String cfi) {
        if (!TextUtils.isEmpty(cfi)) {
            Matcher matcher = CFI_PATTERN.matcher(cfi);
            if (matcher.find()) {
                int itemRef = Integer.valueOf(matcher.group(1));
                return getItemPath(itemRef / 2 - 1); // see generateCfi method, there is a formula itemRef = (itemIndex + 1) * 2
            }
        }
        return null;
    }

    /**
     * @return full path from root (epub://root/bla.html)
     */
    public String getItemUrl(int itemIndex) {
        return EPUB_SCHEME + getItemPath(itemIndex);
    }

    /// Links handling and loading stuff

    public boolean isInternalLink(String url) {
        return url.startsWith(EPUB_SCHEME);
    }

    /**
     * Extracts images, css etc from epub to pass it back to webview
     */
    public WebResourceResponse getWebResource(String url) {
        Log.d(LOG_TAG, "getResource " + url);

        String fileName = url.substring(EPUB_SCHEME.length());

        try {
            String mimeType = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
            if (extension != null) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                mimeType = mime.getMimeTypeFromExtension(extension);
            }

            return new WebResourceResponse(mimeType, "utf-8", getInputStream(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * currently we pass html to webview as a string
     */
    public String getItemHtml(int itemIndex) {
        try {
            InputStream stream = getInputStream(getItemPath(itemIndex));
            if (stream != null)
                return IOUtils.toString(stream, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * used for html items, and also images css etc.
     */
    public InputStream getInputStream(String fileName) throws IOException {
        return getEpubFile().getStream(fileName);
    }

    /// preview fragment checking

    /**
     * this will be needed for preview fragment checking; epub file loads the stream only once and than caches it.
     */
    public void cacheItemData() {
        getEpubFile().loadStream(getCurrentItemPath());
    }

    /**
     * if item is empty, it means we are out of preview fragment.
     * Assumes that loadCurrentItem has been previously called.
     */
    public boolean isCurrentItemEmpty() {
        return getEpubFile().isItemStreamEmpty(getCurrentItemPath());
    }

    /// cfi and bookmark generation

    /**
     * used for markers
     *
     * @return cfi like /6/2!/,/2/4/6/3:211,/2/4/6/3:219
     */
    public String generateCfi(String startCfi, String endCfi) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        org.w3c.dom.Document opfXml = getEpubFile().getOpf();
        String XPATH_EXPRESSION = "count(/package/spine/preceding-sibling::*)";
        int spineNumber = (((Double) xPath.evaluate(XPATH_EXPRESSION, opfXml, XPathConstants.NUMBER)).intValue() + 1) * 2;
        XPATH_EXPRESSION = "/package/spine//itemref[" + currentItemIndex + "]/@id";
        String itemRefId = ((String) xPath.evaluate(XPATH_EXPRESSION, opfXml, XPathConstants.STRING));

        itemRefId = TextUtils.isEmpty(itemRefId) ? "" : "[" + itemRefId + "]";
        int itemRefNumber = (currentItemIndex + 1) * 2;
//        Log.i(LOG_TAG, "spineNumber " + spineNumber + " itemRefId " + itemRefId + " itemRefNumber " + itemRefNumber);
        return "/" + spineNumber + "/" + itemRefNumber + itemRefId + "!/," + startCfi + "," + endCfi;
    }

    /**
     * @return bookmark with format like "d=vgElOTYR&i=RoW6SOwD:0"
     */
    public String generateBookmark(String epubName) {
        return "d=" + epubName + "&i=" + getCurrentItemId() + ":" + (progressInItem * 100);
    }

    public int getCachedTextLength(int itemIndex) {
        return epubItems.get(itemIndex).getTextLength();
    }

    public void cacheTextLength(int itemIndex, int textLength) {
        epubItems.get(itemIndex).setTextLength(textLength);
    }

    /// chapters

    public static String getOpfItemIdByBookmark(String bookmark) {
        Matcher matcher = BOOKMARK_PATTERN.matcher(bookmark);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private NavPoint[] lazyGetChapters() {
        if (chapters == null)
            chapters = metadata.parseChapters();
        return chapters;
    }

    private NavPoint getChapter(int itemIndex) {
        final NavPoint[] chapters = lazyGetChapters();
        final int chapterIndex = metadata.getItemIndex(getItemId(itemIndex));
        if (chapters == null || chapters.length <= chapterIndex) // chapters can still be null here if parseChapters() returned null
            return null;
        return chapters[chapterIndex];
    }

    public String getCurrentChapterTitle() {
        return getChapterTitle(currentItemIndex);
    }

    public String getChapterTitleByProgress(int progress) {
        return getChapterTitle(epubItems.getIndex(bookProgress2Offset(progress)));
    }

    private String getChapterTitle(int itemIndex) {
        final NavPoint chapter = getChapter(itemIndex);
        return chapter == null ? null : chapter.getTitle();
    }

    public interface Listener {

        void onProgressChanged();

        void onEndOfBook();
    }

}
