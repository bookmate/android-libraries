/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   24.12.15
 */
package com.bookmate.libs.epub;

import org.w3c.dom.Document;

import javax.xml.xpath.XPath;

public abstract class AbstractEpubFile implements EpubFile {
    String mRootPath;
    String mOpfPath;
    XPath mXPath;

    String getRootPath() {
        if (mRootPath == null)
            mRootPath = IOUtils.getPath(getOpfPath());
        return mRootPath;
    }

    String getOpfPath() {
        if (mOpfPath != null)
            return mOpfPath;

        try {
            org.w3c.dom.Document doc = getContainer();
            mOpfPath = mXPath.evaluate("//rootfile[@media-type='application/oebps-package+xml']/@full-path", doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mOpfPath;
    }

    abstract Document getContainer();
}
