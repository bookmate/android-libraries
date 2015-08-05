/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.07.15
 */
package com.bookmate.libs.imageviewer.gestures;

import android.view.ScaleGestureDetector;

public class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    protected final ImageViewerGesturesHelper gestures;
    protected boolean detectShowHideGesture = true;
    protected boolean ignoreRestOfScaling;

    public ScaleGestureListener(ImageViewerGesturesHelper gestures) {
        this.gestures = gestures;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        final float scaleFactor = detector.getScaleFactor();

        if (detectShowHideGesture) {
            boolean result = gestures.imageViewer.onShowGesture(detector.getFocusX(), detector.getFocusY());
            if (gestures.config.showOnPinchZoom && scaleFactor > 1 && result) {
                ignoreRestOfScaling = true;
                detectShowHideGesture = false;
            } else if (gestures.config.hideOnPinchZoom && gestures.imageViewer.isActive() && scaleFactor <= 1 && !gestures.imageViewer.isUpScaled()) { // <= is important here! otherwise doesn't work on some devices
                if (hideThresholdPassed(scaleFactor)) {
                    gestures.imageViewer.hide();
                    ignoreRestOfScaling = true;
                    detectShowHideGesture = false;
                } else
                    return false; // we need to accumulate scale factor a little bit more: otherwise we may hide image, when user really intended to scale up
            } else
                detectShowHideGesture = false;
        }

        if (!ignoreRestOfScaling)
            gestures.imageViewer.scale(scaleFactor);

        return true;
    }

    protected static final float HIDE_THRESHOLD = 0.85f;

    protected boolean hideThresholdPassed(float scaleFactor) {
        return scaleFactor <= HIDE_THRESHOLD;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        ignoreRestOfScaling = false;
        detectShowHideGesture = gestures.config.hideOnPinchZoom || gestures.config.showOnPinchZoom;
    }
}
