/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   31.07.15
 */
package com.bookmate.libs.imageviewer.helpers;

import android.graphics.RectF;
import android.view.View;

/**
 * Handles floating point to int conversion issues
 */
public class RectCalculations {

    public static float fitViewBoundaries(float coord, int viewDimension, float imageDimension) {
        if (viewDimension > imageDimension)
            return viewDimension / 2;

        if (coord > imageDimension / 2) // left/top
            return imageDimension / 2;

        if (coord + imageDimension / 2 < viewDimension) // right/bottom
            return viewDimension - imageDimension / 2;

        return coord;
    }

    public static float calcScrollToEdgeCoord(float curCoord, int direction, int viewDimension, float imageDimension) {
        final float coord = direction == 0 ? curCoord : (direction > 0 ? imageDimension / 2 : viewDimension - imageDimension / 2);
        return fitViewBoundaries(coord, viewDimension, imageDimension);
    }

    public static float calcScrollOutsideEdgeCoord(float curCoord, int direction, int viewDimension) {
        return direction == 0 ? curCoord : curCoord + viewDimension * (direction > 0 ? 1 : -1);
    }

    public static RectF getImageRectAtViewCenter(View view, float imageWidth, float imageHeight) {
        return new RectF((view.getWidth() - imageWidth) / 2, (view.getHeight() - imageHeight) / 2,
                (view.getWidth() + imageWidth) / 2, (view.getHeight() + imageHeight) / 2);
    }

    /**
     * if currentRect equals to one of rects, return the next greater rect (or smallest rect if current is already the biggest).
     * else return next smaller rect
     *
     * @param rects must be sorted by width
     */
    public static RectF getClosestRectBySize(RectF currentRect, RectF[] rects) {
        int rectId = 0;
        for (int i = rects.length - 1; i >= 0; i--)
            if (almostGreater(currentRect.width(), rects[i].width())) {
                rectId = i;
                break;
            }

        return sameSize(currentRect, rects[rectId]) ? rects[(rectId + 1) % rects.length] : rects[rectId];
    }

    public static int getSameSizeRectId(RectF currentRect, RectF[] rects) {
        for (int i = 0; i < rects.length; i++)
            if (sameSize(currentRect, rects[i]))
                return i;

        return -1;
    }

    public static float squareDistance2CenterRatio(View view, float x, float y) {
        final float dx = x - view.getWidth() / 2, dy = y - view.getHeight() / 2;
        return (dx * dx + dy * dy) * 2 / (view.getWidth() * view.getWidth() + view.getHeight() * view.getHeight());
    }

    /// floating point errors handling methods

    public static boolean sameCenter(RectF r1, RectF r2) {
        return almostEqual(r1.centerX(), r2.centerX()) && almostEqual(r1.centerY(), r2.centerY());
    }

    public static boolean sameSize(RectF r1, RectF r2) {
        return almostEqual(r1.width(), r2.width()) && almostEqual(r1.height(), r2.height());
    }

    public static boolean lessSize(RectF r1, RectF r2) {
        return almostGreater(r2.width(), r1.width()) || almostGreater(r2.height(), r1.height());
    }

    public static boolean equal(RectF r1, RectF r2) {
        return sameCenter(r1, r2) && sameSize(r1, r2);
    }

    public static boolean almostGreater(float a, float b) {
        return a > b || almostEqual(a, b);
    }

    public static boolean almostEqual(float a, float b) {
        return Math.abs(a - b) <= 1;
    }

}
