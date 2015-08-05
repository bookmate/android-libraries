/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   30.07.15
 */
package com.netimen.ui.imageviewer.gestures;

import android.view.MotionEvent;

import com.netimen.ui.base.Utils;

public class FlingHelper {
    protected final ImageViewerGesturesHelper gestures;

    protected float startImageX, startImageY;

    public FlingHelper(ImageViewerGesturesHelper gestures) {
        this.gestures = gestures;
    }

    protected static final float MIN_FLING_DISTANCE_DP = 100;
    protected static final float MIN_FLING_VELOCITY = 4000;

    @SuppressWarnings("UnusedParameters")
    public boolean onDown(MotionEvent e) {
        startImageX = gestures.imageViewer.getImageX();
        startImageY = gestures.imageViewer.getImageY();
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (gestures.multiTouchHappened)
            return false;

        final float distanceX = distanceDp(e1.getX(), e2.getX()), distanceY = distanceDp(e1.getY(), e2.getY());
        final int xDirection = flingBigEnough(distanceX, velocityX) ? (int) velocityX : 0, yDirection = flingBigEnough(distanceY, velocityY) ? (int) velocityY : 0;
        if (xDirection != 0 || yDirection != 0) { // we need to setup such animation speed, so that it corresponds to previous scrolling image speed.
            final long time = System.currentTimeMillis() - gestures.scrollStartTime;
            final float xVelocity = Math.abs(gestures.imageViewer.getImageX() - startImageX) / time;
            final float yVelocity = Math.abs(gestures.imageViewer.getImageY() - startImageY) / time;
            gestures.imageViewer.scrollToEdgeOrThrowAway(xDirection, yDirection, xVelocity, yVelocity, startImageX, startImageY);
            return true;
        }
        return false;
    }

    protected boolean flingBigEnough(float distance, float velocity) {
        return distance >= MIN_FLING_DISTANCE_DP && Math.abs(velocity) >= MIN_FLING_VELOCITY;
    }

    protected float distanceDp(float val1, float val2) {
        return Utils.px2dp(gestures.imageViewer.getContext(), Math.abs(val1 - val2));
    }
}
