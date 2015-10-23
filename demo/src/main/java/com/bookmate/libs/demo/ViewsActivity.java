/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.10.15
 */
package com.bookmate.libs.demo;

import android.app.Activity;

import com.bookmate.libs.placeholders.EmptyView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_views)
public class ViewsActivity extends Activity {
    @ViewById
    EmptyView emptyView, emptyView2;

    @AfterViews
    void ready() {
        emptyView.showNetworkError(null);
        emptyView2.showNetworkError(null);
    }
}
