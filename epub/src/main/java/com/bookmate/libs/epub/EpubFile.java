package com.bookmate.libs.epub;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by khmelev on 29.04.14.
 * http://www.idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#Section2.3
 */
public interface EpubFile {

    /**
     * OPF is the main navigation file. It contains manifest, describing all the files (images, html files etc) in this epub. And 'spine' element, providing book navigation.
     */
    Document getOpf();

    /**
     * NCX provides contents (names of chapters etc).
     */
    Document getNcx();

    /**
     * @return item size in bytes
     */
    long getSize(String name);

    /**
     * opens an item and returns a stream.
     */
    InputStream getStream(String name) throws IOException;

    /**
     * actually needed in remote epub only for determining if the item stream is empty (server sends empty item when user doesn't have subscription). This is needed for determining the end of preview fragment.
     */
    void loadStream(String name);

    /**
     * actually needed in remote epub only. Empty item means we are at the end of the preview fragment. This method assumes that loadStream has been previously called.
     */
    boolean isItemStreamEmpty(String name);
}
