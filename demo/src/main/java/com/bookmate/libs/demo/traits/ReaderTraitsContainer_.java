/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.08.15
 */
package com.bookmate.libs.demo.traits;

import android.content.Context;

import com.bookmate.libs.demo.traits.bookmate.BookmateReader;
import com.bookmate.libs.demo.traits.bookmate.BookmateReaderComponent_;
import com.bookmate.libs.demo.traits.bookmate.DaggerBookmateReaderComponent_;

public class ReaderTraitsContainer_ {
    public final SelectionTraitHelper_ selectionTraitHelper;
    public final MarkersTraitHelper_ markersTraitHelper;

    public ReaderTraitsContainer_(Context context, BookmateReader bookmateReader) {
        BookmateReaderComponent_ readerComponent = DaggerBookmateReaderComponent_.builder().bookmateReader(bookmateReader).build();

        final SelectionTrait_ selectionTrait = SelectionTrait_.getInstance_(context);
        readerComponent.inject(selectionTrait);
        selectionTraitHelper = new SelectionTraitHelper_(selectionTrait);

        final MarkersTrait_ markersTrait = MarkersTrait_.getInstance_(context);
        readerComponent.inject(markersTrait);
        markersTraitHelper = new MarkersTraitHelper_(markersTrait);
    }
}
