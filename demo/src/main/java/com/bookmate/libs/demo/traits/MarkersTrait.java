/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   16.08.15
 */
package com.bookmate.libs.demo.traits;

import com.bookmate.libs.demo.traits.bookmate.BookmateReaderTrait;
import com.bookmate.libs.demo.traits.readercode.GetTappedMarkerColor;
import com.bookmate.libs.demo.traits.readercode.PageTurn;
import com.bookmate.libs.traits.DataRequest;
import com.bookmate.libs.traits.Event;

import org.androidannotations.annotations.EBean;

import java.util.List;

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
    void badParameterError(int event) {
    }

    @Event
    void badParameterError(List<String> event) {
    }
//    @Event
//    void badEventError() {
//
//    }

    @DataRequest
    int getTappedMarkerColor(GetTappedMarkerColor request) {
        return 0;
    }

    @DataRequest
    Integer getTappedMarkerColor() {
        return 1;
    }

    @DataRequest(GetTappedMarkerColor.class)
    int tappedMarkerColor() {
        return 2;
    }

//    @DataRequest cur test incorrect
//    Document tappedMarkerColor(GetTappedMarkerColor request) {
//        return null;
//    }
}
