/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   03.08.15
 */
package com.bookmate.libs.imageviewer.gestures;

import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bookmate.libs.imageviewer.ImageProvider;
import com.bookmate.libs.imageviewer.ImageViewer;

/**
 * tries to find an ImageView in layout tree at specified point
 */
public class SearchHierarchyImageProvider implements ImageProvider {
    protected final ImageViewer imageViewer;
    private ImageView currentImageView;

    public SearchHierarchyImageProvider(ImageViewer imageViewer) {
        this.imageViewer = imageViewer;
    }

    @Override
    public void findImageAndShow(float x, float y) {
        final ViewGroup parent = (ViewGroup) imageViewer.getParent();

        currentImageView = recursivelyFindImageView(parent, x, y);
        if (currentImageView != null)
            imageViewer.showImage(currentImageView.getDrawable());
    }

    @Override
    public RectF getInitialImageRect() {
        return relativeRect((View) imageViewer.getParent(), currentImageView);
    }

    @Nullable
    protected static ImageView recursivelyFindImageView(ViewGroup parent, float x, float y) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            final View view = parent.getChildAt(i);

            if (isPointInsideView(view, x, y)) {
                if (view instanceof ImageView)
                    return (ImageView) view;
                else if (view instanceof ViewGroup)
                    return recursivelyFindImageView((ViewGroup) view, x, y);
                break;
            }
        }
        return null;
    }

    public static RectF relativeRect(View ancestor, View view) {
        int[] location = new int[2], ancestorLocation = new int[2];

        view.getLocationOnScreen(location);
        ancestor.getLocationOnScreen(ancestorLocation);

        location[0] -= ancestorLocation[0];
        location[1] -= ancestorLocation[1]; // now location is relative to ancestor

        return new RectF(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
    }

    public static boolean isPointInsideView(View view, float x, float y) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);

        return (x > location[0] && x < (location[0] + view.getWidth())) &&
                (y > location[1] && y < (location[1] + view.getHeight()));
    }
}
