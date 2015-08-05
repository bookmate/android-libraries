/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.07.15
 */
package com.bookmate.libs.imageviewer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bookmate.libs.imageviewer.gestures.ImageViewerGesturesHelper;
import com.bookmate.libs.imageviewer.gestures.SearchHierarchyImageProvider;
import com.bookmate.libs.imageviewer.helpers.ImagePositionHelper;
import com.bookmate.libs.imageviewer.helpers.ImageRectAnimationHelper;
import com.bookmate.libs.imageviewer.helpers.RectCalculations;

public class ImageViewer extends ImageView {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = ImageViewer.class.getSimpleName();

    protected final ImagePositionHelper positionHelper;
    protected final ImageRectAnimationHelper animationHelper;
    protected ImageViewerGesturesHelper gesturesHelper;
    protected ImageProvider imageProvider;


    public ImageViewer(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageViewer);
        final int animationDuration = a.getInt(R.styleable.ImageViewer_animationDuration, getResources().getInteger(android.R.integer.config_mediumAnimTime));
        a.recycle();

        if (getBackground() == null)
            setBackgroundColor(Color.argb(255, 0, 0, 0));

        imageProvider = createImageProvider();
        positionHelper = createPositionHelper();
        animationHelper = createAnimationHelper(animationDuration);
        gesturesHelper = createGesturesHelper();

        setScaleType(ScaleType.MATRIX);
        setVisibility(INVISIBLE);
    }

    public void showImage(Drawable image) {
        setImageDrawable(image);

        animationHelper.animateVisibility(true, true);
    }

    public void hide() {
        if (isActive())
            animationHelper.animateVisibility(true, false);
    }

    /**
     * means that image viewer is currently visible, and opening animation has finished
     */
    public boolean isActive() {
        return animationHelper.isActive;
    }

    /**
     * for images lesser than view, simply means that currentScale is > 1, otherwise means that one of image's dimensions is bigger than view's
     */
    public boolean isUpScaled() {
        return positionHelper.isUpScaled();
    }

    public void scale(float scaleFactor) {
        if (isActive() && scaleFactor > 1 || positionHelper.isUpScaled())
            positionHelper.scale(scaleFactor);
    }

    public void move(float deltaX, float deltaY) {
        if (isActive())
            positionHelper.move(deltaX, deltaY);
    }

    /**
     * If the image is smaller than the view, centers it inside the view. Otherwise moves image so that there are no gaps between view boundary and the image.
     */
    public void fitViewBoundaries() {
        if (isActive())
            animationHelper.animateImageRect(positionHelper.getCurrentRect(), positionHelper.getFitViewBoundariesRect());
    }

    /**
     * if image is not centered, just centers it
     * other wise cycles it's size: center inside - fit smaller dimension - fit greater dimension
     */
    public void cycleCenterPositions() {
        if (isActive())
            animationHelper.animateImageRect(positionHelper.getCurrentRect(), positionHelper.getCenterPositionRect());
    }

    /**
     * @param xVelocity previous movement velocity (pixels/ms). We setup animation duration, so that the image speed will not change
     * @param yVelocity previous movement velocity (pixels/ms). We setup animation duration, so that the image speed will not change
     * @param xStart    start X of image (before gesture began) (needed for interpolator determination)
     */
    public void scrollToEdgeOrThrowAway(int xDirection, int yDirection, float xVelocity, float yVelocity, float xStart, float yStart) {
        if (isActive()) {
            if (positionHelper.getCurrentWidth() < getWidth() || positionHelper.getCurrentHeight() < getHeight()) {// throw away small image
                animationHelper.animateImageRect(positionHelper.getCurrentRect(), positionHelper.getScrollOutsideEdgeRect(xDirection, yDirection), xVelocity, yVelocity, xStart, yStart);
                animationHelper.animateVisibility(false, false);
            } else
                animationHelper.animateImageRect(positionHelper.getCurrentRect(), positionHelper.getScrollToEdgeRect(xDirection, yDirection), xVelocity, yVelocity, xStart, yStart);
        }
    }

    public float getImageX() {
        return positionHelper.currentX;
    }

    public float getImageY() {
        return positionHelper.currentY;
    }

    public boolean pointInImage(float x, float y) {
        return positionHelper.getCurrentRect().contains(x, y);
    }

    /// config methods

    @SuppressWarnings("unused")
    public void setGesturesHelper(ImageViewerGesturesHelper gesturesHelper) {
        this.gesturesHelper = gesturesHelper;
    }

    @SuppressWarnings("unused")
    public void setImageProvider(ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
    }

    /**
     * @param x raw event coordinate
     */
    public boolean onShowGesture(float x, float y) {
        if (getVisibility() != View.VISIBLE) { // don't call isActive, because we need to check animating state also
            imageProvider.findImageAndShow(x, y);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return gesturesHelper.onTouchEvent(event);
    }

    ///


    /**
     * here we try to put image in same position as it was before layout: if it was fitting a dimension, making it fit a dimension again etc
     */
    @Override
    public void layout(int l, int t, int r, int b) { // don't use onLayout: it gets called after layout, so I can't calc important rects correctly
        RectF[] importantRectsBeforeLayout = null;
        if (isActive()) // calc only if image is showing now
            importantRectsBeforeLayout = positionHelper.getImportantRects();

        super.layout(l, t, r, b);

        if (isActive())
            animationHelper.animateImageRect(positionHelper.getCurrentRect(), positionHelper.getAfterLayoutRect(importantRectsBeforeLayout));
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        fadeOnMovement();

        super.setImageMatrix(matrix);
    }

    ///

    protected void fadeOnMovement() {
        if (!animationHelper.isShowing) // ignoring opening animation
            return;

        final float distanceRatio = RectCalculations.squareDistance2CenterRatio(this, getImageX(), getImageY());
        getBackground().setAlpha((int) (255 * (1 - Math.min(distanceRatio, .5f))));
    }

    protected ImageProvider createImageProvider() {
        return new SearchHierarchyImageProvider(this);
    }

    protected ImageViewerGesturesHelper createGesturesHelper() {
        return new ImageViewerGesturesHelper(this, ImageViewerGesturesHelper.Config.turnOnEverything());
    }

    protected ImagePositionHelper createPositionHelper() {
        return new ImagePositionHelper(this);
    }

    protected ImageRectAnimationHelper createAnimationHelper(int animationDuration) {
        return new ImageRectAnimationHelper(this, imageProvider, positionHelper, animationDuration);
    }

}
