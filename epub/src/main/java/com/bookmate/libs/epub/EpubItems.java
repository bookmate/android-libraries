package com.bookmate.libs.epub;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by khmelev on 30.04.14.
 * Provides navigation between epub items (spine section in OPF).
 */
class EpubItems {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = EpubItems.class.getSimpleName();

    private static final String ITEM_REFS_XPATH = "//spine/itemref";
    private static final String ITEMS_XPATH = "//item";

    /**
     * contains list of all the epub items (id, path and size) listed in opf <spine>.
     */
    private final List<EpubItem> items = new ArrayList<>();

    /**
     * sumtotal of sizes of all the epub items (html, NOT including images).
     * In case of remote epub file we use here size sent from server, so we don't actually calculate them.
     */
    private long totalSize = 0;

    public EpubItems(EpubFile file) throws XPathExpressionException {
        super();

        Document opfXml = file.getOpf();

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        NodeList itemRefs = (NodeList) xPath.evaluate(ITEM_REFS_XPATH, opfXml, XPathConstants.NODESET);
        NodeList items = (NodeList) xPath.evaluate(ITEMS_XPATH, opfXml, XPathConstants.NODESET);

        for (int i = 0; i < itemRefs.getLength(); i++) {
            Node itemRef = itemRefs.item(i);
            String navId = itemRef.getAttributes().getNamedItem("idref").getTextContent();

            Node item = findItemById(items, navId);

            if (item != null) {
                String navPath = item.getAttributes().getNamedItem("href").getTextContent();
                long size = file.getSize(navPath);
                this.items.add(new EpubItem(navId, navPath, size, totalSize));
                totalSize += size;
            }
        }
    }

    private Node findItemById(NodeList items, String id) {
        for (int i = 0; i < items.getLength(); i++) {
            Node item = items.item(i);
            String itemId = item.getAttributes().getNamedItem("id").getTextContent();

            if (id.equals(itemId))
                return item;
        }

        return null;
    }

    public int getItemsCount() {
        return items.size();
    }

    public long getTotalSize() {
        return totalSize;
    }

    public EpubItem get(int itemIndex) {
        return items.get(itemIndex);
    }

    private EpubItem get(String id) {
        for (EpubItem epubItem : items) {
            if (epubItem.getId().equals(id))
                return epubItem;
        }

        return null;
    }

//    public EpubItem getByPath(String path) {
//        for (EpubItem epubItem : items) {
//            if (epubItem.getPath().equals(path))
//                return epubItem;
//        }
//
//        return null;
//    }

    public int getIndex(String id) {
        EpubItem epubItem = get(id);
        if (epubItem != null)
            return items.indexOf(epubItem);

        return -1;
    }

    /**
     * find index of item which contains the desired offset.
     *
     * @param offset in bytes
     * @return item index
     */
    public int getIndex(long offset) {
        return Arrays.binarySearch(items.toArray(), offset, new Comparator<Object>() { // items are sorted by sizeUpToThisItem, so we can perform a binary search here.
            @Override
            public int compare(Object lhs, Object rhs) {
                final EpubItem epubItem = (EpubItem) lhs;
                final Long offset = (Long) rhs;
                return offset < epubItem.getSizeUpToThisItem() ? 1 : (offset > epubItem.getSizeUpToThisItem() + epubItem.getSize() ? -1 : 0);
            }
        });
    }

    /**
     * @return sum of the size of items 0 <= itemIndex < @upToIndex
     */
    public long sumSizes(int upToIndex) {
        long sumSizes = 0;
        for (int i = 0; i < upToIndex; i++)
            sumSizes += get(i).getSize();
        return sumSizes;
    }

    /**
     * @return sum of the text lengths of items 0 <= itemIndex < @upToIndex
     */
    public long sumTextLengths(int upToIndex) {
        long sumTextLengths = 0;
        for (int i = 0; i < upToIndex; i++)
            sumTextLengths += get(i).getSize();
        return sumTextLengths;
    }

}
