/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   19.08.15
 */
package com.bookmate.libs.demo.traits.bookmate;

import com.bookmate.libs.demo.traits.MarkersTrait;
import com.bookmate.libs.demo.traits.ReaderFragment;
import com.bookmate.libs.demo.traits.ReaderTraitsContainer_;
import com.bookmate.libs.demo.traits.SelectionTrait;
import com.bookmate.libs.demo.traits.readercode.ReadingSystem;
import com.bookmate.libs.traits.Traits;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

@EFragment
@Traits(traits = {SelectionTrait.class, MarkersTrait.class}, sharedFields = {ReadingSystem.class})
public class BookmateReaderFragment extends ReaderFragment {

    @FragmentArg
    BookmateReader bookmateReader;

    private ReaderTraitsContainer_ readerTraitsContainer_;

    @AfterInject
    protected void afterInject() {
        readerTraitsContainer_ = new ReaderTraitsContainer_(getActivity(), bookmateReader);
    }
}
