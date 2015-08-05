/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.07.15
 */
package com.netimen.ui.demo;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.bookmate.libs.imageviewer.ImageViewer;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_image_viewer)
public class ImageViewerActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = ImageViewerActivity.class.getSimpleName();

    @ViewById
    ImageViewer imageViewer;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return imageViewer.onTouchEvent(event);
    }

}
