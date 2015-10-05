/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   05.10.15
 */
package com.bookmate.libs.demo;

import android.support.v7.app.AppCompatActivity;

import com.bookmate.libs.demo.traits.bookmate.BookmateReader;
import com.bookmate.libs.demo.traits.bookmate.BookmateReaderFragment_;
import com.bookmate.libs.demo.traits.readercode.Document;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_reader)
public class ReaderActivity extends AppCompatActivity {

    @AfterViews
    void ready() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, BookmateReaderFragment_.builder().bookmateReader(new BookmateReader(new Document(), "bookmark", "discovered")).build(), "tag")
                .commit();
    }
}
