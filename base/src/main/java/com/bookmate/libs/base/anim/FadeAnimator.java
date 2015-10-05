/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   01.07.15
 */
package com.bookmate.libs.base.anim;


/**
 * implements fade in/fade out animation and supports checking against to frequent restarting.
 */
public class FadeAnimator extends AbstractFadeAnimator {
    public FadeAnimator() {
    }

    public FadeAnimator(int animationDuration) {
        super(animationDuration);
    }

    protected void performAnimateVisibility(final Anim anim) {
        anim.view.setAlpha(anim.show ? 0 : 1);

        anim.view.animate().alpha(anim.show ? 1 : 0).setDuration(animationDuration).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (!anim.show)
                    anim.view.setAlpha(1); // without this we get strange results if we call view.setVisibility(VISIBLE) somewhere in code after animateVisibility
                onAnimationEnd(anim);
            }
        });
    }
}
