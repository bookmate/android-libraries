/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.07.15
 */
package com.netimen.ui.imageviewer.gestures;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class OneFingerGestureListener extends GestureDetector.SimpleOnGestureListener {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = OneFingerGestureListener.class.getSimpleName();

    protected final ImageViewerGesturesHelper gestures;
    protected final ScrollHelper scrollHelper;
    protected final FlingHelper flingHelper;

    public OneFingerGestureListener(ImageViewerGesturesHelper gestures) {
        this.gestures = gestures;
        scrollHelper = createScrollHelper();
        flingHelper = createFlingHelper();
    }

    protected ScrollHelper createScrollHelper() {
        return new ScrollHelper(gestures);
    }

    protected FlingHelper createFlingHelper() {
        return new FlingHelper(gestures);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (gestures.imageViewer.isActive()) {
            if (gestures.config.hideOnDoubleTapOutside && !pointInImage(e)) {
                gestures.imageViewer.hide();
            } else
                gestures.imageViewer.cycleCenterPositions();

            return true;
        }

        if (gestures.config.showOnDoubleTap) {
            boolean result;
            gestures.imageViewer.onShowGesture(e.getRawX(), e.getRawY());
        }
        return gestures.config.showOnDoubleTap;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (gestures.config.showOnLongPress) {
            boolean result;
            gestures.imageViewer.onShowGesture(e.getRawX(), e.getRawY());
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (gestures.config.hideOnSingleTap)
            gestures.imageViewer.hide();
        return gestures.config.hideOnSingleTap;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        boolean result = flingHelper.onDown(e);
        return scrollHelper.onDown(e) | result;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return scrollHelper.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return flingHelper.onFling(e1, e2, velocityX, velocityY);
    }

    protected boolean pointInImage(MotionEvent e) {
        int location[] = new int[2];
        gestures.imageViewer.getLocationOnScreen(location);
        return gestures.imageViewer.pointInImage(e.getRawX() - location[0], e.getRawY() - location[1]);
    }

}
