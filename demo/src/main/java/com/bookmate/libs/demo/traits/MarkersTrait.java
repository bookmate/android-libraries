/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   16.08.15
 */
package com.bookmate.libs.demo.traits;

import com.bookmate.libs.demo.traits.bookmate.BookmateReaderTrait;
import com.bookmate.libs.demo.traits.readercode.PageTurn;
import com.bookmate.libs.traits.Event;

import org.androidannotations.annotations.EBean;

@EBean
public class MarkersTrait extends BookmateReaderTrait {
    @Event(PageTurn.class)
    void pageTurned() {
    }

    @Event
    void onPageTurn() {
    }

    @Event
    void pageTurn() {
    }

    @Event
    void onPageTurned(PageTurn event) {
    }
}
