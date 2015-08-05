/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.07.15
 */
package com.bookmate.libs.imageviewer.gestures;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.bookmate.libs.imageviewer.ImageViewer;

public class ImageViewerGesturesHelper {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = ImageViewerGesturesHelper.class.getSimpleName();

    protected final GestureDetector gestureDetector;
    protected final ScaleGestureDetector scaleGestureDetector;
    protected final ImageViewer imageViewer;
    protected final Config config;
    protected boolean multiTouchHappened;
    /**
     * needed for fling velocity calculation
     */
    protected long scrollStartTime;

    public static class Config {
        boolean showOnDoubleTap, showOnLongPress, showOnPinchZoom, hideOnSingleTap, hideOnPinchZoom, hideOnDoubleTapOutside;

        public Config showOnDoubleTap() {
            showOnDoubleTap = true;
            return this;
        }

        public Config showOnLongPress() {
            showOnLongPress = true;
            return this;
        }

        public Config showOnPinchZoom() {
            showOnPinchZoom = true;
            return this;
        }

        public Config hideOnSingleTap() {
            hideOnSingleTap = true;
            return this;
        }

        public Config hideOnPinchZoom() {
            hideOnPinchZoom = true;
            return this;
        }

        public Config hideOnDoubleTapOutside() {
            hideOnDoubleTapOutside = true;
            return this;
        }

        public static Config turnOnEverything() {
            return new Config().showOnDoubleTap().showOnLongPress().showOnPinchZoom().hideOnSingleTap().hideOnPinchZoom().hideOnDoubleTapOutside();
        }
    }

    public ImageViewerGesturesHelper(final ImageViewer imageViewer, final Config config) {
        this.imageViewer = imageViewer;
        this.config = config;

        gestureDetector = new GestureDetector(imageViewer.getContext(), createGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(imageViewer.getContext(), createScaleGestureListener());
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = scaleGestureDetector.onTouchEvent(event);
        result |= gestureDetector.onTouchEvent(event); // don't simplify it to sgd.onTouch | gd.onTouch, otherwise gd.onTouch won't be always called!

        switch (event.getActionMasked()) { // don't call getAction if you want to detect POINTER_DOWN
            case MotionEvent.ACTION_POINTER_DOWN:
                multiTouchHappened = true; // we can't detect multitouch in the OnGestureListener, so we have to do it here
                break;
            case MotionEvent.ACTION_UP:
                imageViewer.fitViewBoundaries();
            case MotionEvent.ACTION_DOWN: // no break intentionally!
                multiTouchHappened = false;
                break;
        }

        return result;
    }

    protected GestureDetector.OnGestureListener createGestureListener() {
        return new OneFingerGestureListener(this);
    }

    protected ScaleGestureDetector.OnScaleGestureListener createScaleGestureListener() {
        return new ScaleGestureListener(this);
    }

}
