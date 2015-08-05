/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.07.15
 */
package com.bookmate.libs.base.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;

public class FadeBackgroundAnimator extends AbstractFadeAnimator {
    private static final String LOG_TAG = FadeBackgroundAnimator.class.getSimpleName();

    public FadeBackgroundAnimator() {
    }

    public FadeBackgroundAnimator(int animationDuration) {
        super(animationDuration);
    }

    @Override
    protected void performAnimateVisibility(final Anim anim) {
        final ValueAnimator animator = ValueAnimator.ofInt(anim.show ? 0 : 255, anim.show ? 255 : 0).setDuration(animationDuration);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FadeBackgroundAnimator.this.onAnimationEnd(anim);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                anim.view.getBackground().setAlpha((Integer) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

}
