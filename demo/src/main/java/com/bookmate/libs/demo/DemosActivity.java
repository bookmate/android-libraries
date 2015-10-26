/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.07.15
 */
package com.bookmate.libs.demo;

import android.app.Activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_demos)
public class DemosActivity extends Activity {
    @AfterViews
    void ready() {
//        ImageViewerActivity_.intent(this).start();
//        ReaderActivity_.intent(this).start();
        PlaceholdersActivity_.intent(this).start();
    }
}
