/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   16.08.15
 */
package com.bookmate.libs.demo.traits;

import com.bookmate.libs.demo.traits.bookmate.BookmateReaderTrait;
import com.bookmate.libs.demo.traits.readercode.Document;
import com.bookmate.libs.demo.traits.readercode.SelectionStart;
import com.bookmate.libs.traits.Event;

import org.androidannotations.annotations.EBean;

@EBean
public class MarkersTrait extends BookmateReaderTrait {
    @Event(Document.class)
    void onDocument() {

    }

    @Event
    void onPageTurn() {

    }

    @Event
    void pageTurn() {

    }

    @Event
    void PageTurn() {

    }

    @Event
    void onSelectionStart(SelectionStart event) {
    }
}
