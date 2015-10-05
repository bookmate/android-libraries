/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   24.07.15
 */
package com.bookmate.libs.base.anim;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.view.View;

public class Animations {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = Animations.class.getSimpleName();

    public static void fadeBackground(View view, boolean show, int animationDuration, final Runnable endAction) {
        new FadeBackgroundAnimator(animationDuration).animateVisibility(view, show, endAction);
    }

//    /**
//     * requires view having {@link android.view.ViewGroup.MarginLayoutParams} (like children of {@link android.widget.FrameLayout} have).
//     */
//    public static void viewBounds(final View view, final Rect startBounds, final Rect endBounds, int animationDuration, final Runnable endAction) {
//        animateRect(startBounds, endBounds, animationDuration, null, new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//                layoutParams.leftMargin = (int) valueAnimator.getAnimatedValue("l");
//                layoutParams.topMargin = (int) valueAnimator.getAnimatedValue("t");
//                layoutParams.width = (int) valueAnimator.getAnimatedValue("w");
//                layoutParams.height = (int) valueAnimator.getAnimatedValue("h");
//                Log.e(LOG_TAG, "AAAA onAnimationUpdate " + layoutParams.leftMargin + " " + layoutParams.width + " " + layoutParams.height);
//                view.requestLayout();
//            }
//        }, endAction);
//    }

    /**
     * helper to get animated value of {@link #animateRect}
     */
    public static RectF getAnimatedRect(ValueAnimator animator) {
        return new RectF((float) animator.getAnimatedValue("l"), (float) animator.getAnimatedValue("t"), (float) animator.getAnimatedValue("r"), (float) animator.getAnimatedValue("b"));
    }

    /**
     * get current rect in updateListener with valueAnimator.getAnimatedValue("l") etc
     *
     * @param interpolator null means default
     */
    public static void animateRect(RectF startRect, RectF endRect, ValueAnimator.AnimatorUpdateListener updateListener, TimeInterpolator interpolator, int animationDuration, final Runnable endAction) {
        ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(
                PropertyValuesHolder.ofFloat("l", startRect.left, endRect.left),
                PropertyValuesHolder.ofFloat("t", startRect.top, endRect.top),
                PropertyValuesHolder.ofFloat("r", startRect.right, endRect.right),
                PropertyValuesHolder.ofFloat("b", startRect.bottom, endRect.bottom))
                .setDuration(animationDuration);

        if (interpolator != null)
            anim.setInterpolator(interpolator);

        if (endAction != null)
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    endAction.run();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        anim.addUpdateListener(updateListener);
        anim.start();
    }
}
