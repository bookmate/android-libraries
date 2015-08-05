/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   30.07.15
 */
package com.netimen.ui.imageviewer.gestures;

import android.view.MotionEvent;

import com.bookmate.libs.base.Utils;

public class ScrollHelper {
    protected final ImageViewerGesturesHelper gestures;

    /**
     * fighting the touch slop: first onScroll event comes with too big distances due to touch slop. So we just skip it to avoid the visual glitch
     * http://stackoverflow.com/a/15401698/190148
     */
    protected boolean firstScrollEvent;

    public ScrollHelper(ImageViewerGesturesHelper gestures) {
        this.gestures = gestures;
    }

    @SuppressWarnings("UnusedParameters")
    public boolean onDown(MotionEvent e) {
        firstScrollEvent = true;
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (gestures.multiTouchHappened)
            return false;

        if (firstScrollEvent) { // fighting touch slop http://stackoverflow.com/a/15401698/190148
            firstScrollEvent = false;
            gestures.scrollStartTime = System.currentTimeMillis();
            return false;
        }

        final float dx = scrollBigEnough(distanceDp(e2.getRawX(), e1.getRawX())) ? -distanceX : 0, // stabilizing: so when we really want to scroll horizontally we ignore small vertical scrolling;
                dy = scrollBigEnough(distanceDp(e2.getRawY(), e1.getRawY())) ? -distanceY : 0;     // - because when I move finger right, distanceX is < 0 for some reason; same for Y
        gestures.imageViewer.move(dx, dy);
        return gestures.imageViewer.isActive(); // if isActive, moving happened
    }

    protected static final float MIN_SCROLL_DISTANCE_DP = 20;

    protected boolean scrollBigEnough(float distance) {
        return distance >= MIN_SCROLL_DISTANCE_DP;
    }

    protected float distanceDp(float val1, float val2) {
        return Utils.px2dp(gestures.imageViewer.getContext(), Math.abs(val1 - val2));
    }
}
