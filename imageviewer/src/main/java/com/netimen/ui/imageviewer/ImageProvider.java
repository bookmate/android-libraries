/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.08.15
 */
package com.netimen.ui.imageviewer;

import android.graphics.RectF;

/**
 * called when user makes show gesture (double tap etc).
 */
public interface ImageProvider {
    /**
     * Needs to find image to show and call {@link ImageViewer#showImage}. Such architecture allows to deferred calling of {@link ImageViewer#showImage} (for instance, when we need to get some image from WebView and then show it).
     */
    void findImageAndShow(float x, float y);

    /**
     * @return position of image on screen before showing (needed for animation).
     */
    RectF getInitialImageRect();
}
