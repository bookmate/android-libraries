/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.07.15
 */
package com.netimen.ui.imageviewer.helpers;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.widget.ImageView;

public class ImagePositionHelper {
    protected final Matrix matrix = new Matrix();
    protected final ImageView imageView;
    protected float currentScale, defaultScale;
    /**
     * image center coordinates
     */
    public float currentX;
    public float currentY;

    public ImagePositionHelper(ImageView imageView) {
        this.imageView = imageView;
    }

    public float getCurrentWidth() {
        return imageView.getDrawable().getIntrinsicWidth() * currentScale;
    }

    public float getCurrentHeight() {
        return imageView.getDrawable().getIntrinsicHeight() * currentScale;
    }

    public float getFitViewBoundariesX() {
        return RectCalculations.fitViewBoundaries(currentX, imageView.getWidth(), getCurrentWidth());
    }

    public float getFitViewBoundariesY() {
        return RectCalculations.fitViewBoundaries(currentY, imageView.getHeight(), getCurrentHeight());
    }

    public RectF getCurrentRect() {
        return getImageRectAtPosition(currentX, currentY);
    }

    /**
     * @return rect of same size, but in position, so there are no gaps between image edge and view
     */
    public RectF getFitViewBoundariesRect() {
        return getImageRectAtPosition(getFitViewBoundariesX(), getFitViewBoundariesY());
    }

    /**
     * moves image to edge, image within view
     */
    public RectF getScrollToEdgeRect(int xDirection, int yDirection) {
        return getImageRectAtPosition(RectCalculations.calcScrollToEdgeCoord(currentX, xDirection, imageView.getWidth(), getCurrentWidth()),
                RectCalculations.calcScrollToEdgeCoord(currentY, yDirection, imageView.getHeight(), getCurrentHeight()));
    }

    /**
     * extrapolates image movement, to move it outside view
     */
    public RectF getScrollOutsideEdgeRect(int xDirection, int yDirection) {
        return getImageRectAtPosition(RectCalculations.calcScrollOutsideEdgeCoord(currentX, xDirection, imageView.getWidth()),
                RectCalculations.calcScrollOutsideEdgeCoord(currentY, yDirection, imageView.getHeight()));
    }

    /**
     * @return position rect corresponding to {@link android.widget.ImageView.ScaleType#CENTER_INSIDE}
     * also remembers the scale
     */
    public RectF getDefaultRect() {
        defaultScale = Math.min(1, Math.min(getAspectRatioX(), getAspectRatioY())); // don't upscale the small images, so comparing with 1
        return getScaledImageRectAtViewCenter(defaultScale);
    }

    /**
     * if image is not centered, returns centered rect of same size
     * other wise cycles it's size: center inside - fit smaller dimension - fit greater dimension
     */
    public RectF getCenterPositionRect() {
        final RectF currentImageRect = getCurrentRect(), defaultImageRect = getDefaultRect();

        if (!RectCalculations.sameCenter(currentImageRect, defaultImageRect))  // just moving to center
            return getScaledImageRectAtViewCenter(currentScale);

        return RectCalculations.getClosestRectBySize(currentImageRect, getImportantRects());
    }

    /**
     * if image is smaller thant "center inside" position, moving it to center inside.
     * if image was fit by some dimension, sets it to fit new dimension
     * otherwise just centers the image
     */
    public RectF getAfterLayoutRect(RectF[] importantRectsBeforeLayout) {
        final RectF currentImageRect = getCurrentRect();

        final int sameSizeRectId = RectCalculations.getSameSizeRectId(currentImageRect, importantRectsBeforeLayout);
        if (sameSizeRectId != -1)
            return getImportantRects()[sameSizeRectId]; // making it fit same dimension as before layout

        if (RectCalculations.lessSize(currentImageRect, importantRectsBeforeLayout[0])) // if image is to small, making it center-inside
            return importantRectsBeforeLayout[0];

        return getScaledImageRectAtViewCenter(currentScale); // just centering it
    }

    public RectF[] getImportantRects() {
        return new RectF[]{getDefaultRect(), getScaledImageRectAtViewCenter(Math.min(getAspectRatioX(), getAspectRatioY())), getScaledImageRectAtViewCenter(Math.max(getAspectRatioX(), getAspectRatioY()))};
    }

    /**
     * setups scale and coordinates to fit the positionRect. Actually image is set to be scaled like {@link android.widget.ImageView.ScaleType#CENTER_INSIDE} inside the rect
     */
    public void setRect(RectF positionRect) {
        currentScale = Math.min(positionRect.width() / imageView.getDrawable().getIntrinsicWidth(), positionRect.height() / imageView.getDrawable().getIntrinsicHeight());
        currentX = positionRect.centerX();
        currentY = positionRect.centerY();
        updateScaleAndPosition();
    }

    /// scale / move

    /**
     * for images lesser than view, simply means that currentScale is > 1, otherwise means that one of image's dimensions is bigger than view's
     */
    public boolean isUpScaled() {
        return currentScale > 1 || getCurrentWidth() > imageView.getWidth() || getCurrentHeight() > imageView.getHeight();
    }

    /**
     * new scale = currentScale * scaleFactor
     */
    public void scale(float scaleFactor) {
        currentScale = Math.max(defaultScale, currentScale * scaleFactor);

        if (scaleFactor < 1) { // otherwise, when we scale down being at the image corner, the image visually moves away
            currentX = getFitViewBoundariesX();
            currentY = getFitViewBoundariesY();
        }

        updateScaleAndPosition();
    }

    /**
     * new x = x + deltaX
     */
    public void move(float deltaX, float deltaY) {
        currentX += deltaX;
        currentY += deltaY;
        updateScaleAndPosition();
    }

    ///

    protected float getAspectRatioY() {
        return (float) imageView.getHeight() / imageView.getDrawable().getIntrinsicHeight();
    }

    protected float getAspectRatioX() {
        return (float) imageView.getWidth() / imageView.getDrawable().getIntrinsicWidth();
    }

    protected RectF getImageRectAtPosition(float x, float y) {
        return new RectF(x - getCurrentWidth() / 2, y - getCurrentHeight() / 2, x + getCurrentWidth() / 2, y + getCurrentHeight() / 2);
    }

    protected RectF getScaledImageRectAtViewCenter(float scale) {
        return RectCalculations.getImageRectAtViewCenter(imageView, imageView.getDrawable().getIntrinsicWidth() * scale, imageView.getDrawable().getIntrinsicHeight() * scale);
    }

    protected void updateScaleAndPosition() {
        matrix.setScale(currentScale, currentScale);
        matrix.postTranslate(currentX - getCurrentWidth() / 2, currentY - getCurrentHeight() / 2);
        imageView.setImageMatrix(matrix);
    }

} // CUR animation config
