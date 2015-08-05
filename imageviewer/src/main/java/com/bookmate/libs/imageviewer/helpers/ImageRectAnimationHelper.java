/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   31.07.15
 */
package com.bookmate.libs.imageviewer.helpers;

import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.animation.BounceInterpolator;

import com.bookmate.libs.base.anim.Animations;
import com.bookmate.libs.imageviewer.ImageProvider;
import com.bookmate.libs.imageviewer.ImageViewer;

public class ImageRectAnimationHelper {
    protected final ImageViewer imageViewer;
    protected final ImageProvider imageProvider;
    protected final ImagePositionHelper positionHelper;
    protected final int animationDuration;
    /**
     * means visible and no animation running
     */
    public boolean isActive;
    /**
     * means visible and showing process finished
     */
    public boolean isShowing;

    public ImageRectAnimationHelper(ImageViewer imageViewer, ImageProvider imageProvider, ImagePositionHelper positionHelper, int animationDuration) {
        this.imageViewer = imageViewer;
        this.imageProvider = imageProvider;
        this.positionHelper = positionHelper;
        this.animationDuration = animationDuration;
    }

    public void animateVisibility(boolean animateRect, final boolean show) {
        isShowing = isActive = false;
        Animations.fadeBackground(imageViewer, show, animationDuration, new Runnable() {
            @Override
            public void run() {
                isShowing = show;
            }
        });

        if (animateRect)
            animateImageRect(show ? imageProvider.getInitialImageRect() : positionHelper.getCurrentRect(), show ? positionHelper.getDefaultRect() : imageProvider.getInitialImageRect(), animationDuration, false, show);
    }

    public void animateImageRect(RectF startRect, RectF endRect) {
        animateImageRect(startRect, endRect, animationDuration, false, true);
    }

    public void animateImageRect(RectF startRect, RectF endRect, int duration, boolean allowBounce, boolean activateAfterAnimation) {
        if (RectCalculations.equal(startRect, endRect)) // discarding equal (or almost equal rects)
            return;

        isActive = false;

        Animations.animateRect(startRect, endRect,
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        positionHelper.setRect(Animations.getAnimatedRect(animation));
                    }
                }, allowBounce ? new BounceInterpolator() : null, duration,
                activateAfterAnimation ? new Runnable() {
                    @Override
                    public void run() {
                        isActive = true;
                    }
                } : null);
    }

    /**
     * we need to setup such animation speed, so that it corresponds to previous scrolling image speed.
     */
    public void animateImageRect(RectF startRect, RectF endRect, float xVelocity, float yVelocity, float xStart, float yStart) {
        final int distanceX = (int) (endRect.centerX() - startRect.centerX()), distanceY = (int) (endRect.centerY() - startRect.centerY());
        int animationDuration = this.animationDuration;

        if (imageViewer.getImageX() == positionHelper.getFitViewBoundariesX() &&
                imageViewer.getImageY() == positionHelper.getFitViewBoundariesY()) { // if we just return to fit position, don't speed up animation (needed when we try to fling left from left edge etc)
            final float xDuration = (distanceX == 0 || xVelocity == 0) ? Float.MAX_VALUE : Math.abs(distanceX) / xVelocity;
            final float yDuration = (distanceY == 0 || yVelocity == 0) ? Float.MAX_VALUE : Math.abs(distanceY) / yVelocity;
            animationDuration = (int) Math.min(xDuration, yDuration);
        }

        animateImageRect(startRect, endRect, animationDuration, movedToSameViewEdge(startRect, endRect, xStart, yStart), true);
    }

    ///

    /**
     * tries to detect the situation when user dragged from left edge in right direction, so the image should return to same position.
     *
     * @param xStart image position before gesture start
     */
    @SuppressWarnings("RedundantIfStatement")
    protected boolean movedToSameViewEdge(RectF startRect, RectF endRect, float xStart, float yStart) {
        final int vw = imageViewer.getWidth(), iw = (int) positionHelper.getCurrentWidth(), vh = imageViewer.getHeight(), ih = (int) positionHelper.getCurrentHeight();

        if (RectCalculations.sameSize(startRect, endRect) &&
                (endRect.width() >= vw && endRect.left != startRect.left && // image moved by X axis, and is wider than the view
                        (endRect.right == vw && RectCalculations.almostEqual(xStart, vw - iw / 2) || endRect.left == 0 && RectCalculations.almostEqual(xStart, iw / 2)) || // image moved to right or left view edge
                        endRect.height() >= vh && endRect.top != startRect.top &&
                                (endRect.bottom == vh && RectCalculations.almostEqual(yStart, vh - ih / 2) || endRect.top == 0 && RectCalculations.almostEqual(yStart, ih / 2))))
            return true;

        return false;
    }

}
