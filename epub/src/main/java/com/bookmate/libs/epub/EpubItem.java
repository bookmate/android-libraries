package com.bookmate.libs.epub;

/**
 * Created by khmelev on 30.04.14.
 * describes an epub item like in OPF manifest section. Also stores item size in bytes
 */
class EpubItem {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = EpubItem.class.getSimpleName();

    private final String id, path;

    /**
     * size of item in bytes
     */
    private final long size;

    /**
     * sum of sizes in bytes of previous items
     */
    private final long sizeUpToThisItem;

    /**
     * Length of text in this item (without tags). Used for calculating the already read size (needed for paywall) and to prevent text length calculation each time, when navigating forth and back with the history.
     */
    private int textLength;


    public EpubItem(String id, String path, long size, long sizeUpToThisItem) {
        this.id = id;
        this.path = path;
        this.size = size;
        this.sizeUpToThisItem = sizeUpToThisItem;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String toString() {
        return "id:" + id + "_path:" + path + "_size:" + size;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    public long getSizeUpToThisItem() {
        return sizeUpToThisItem;
    }
}
