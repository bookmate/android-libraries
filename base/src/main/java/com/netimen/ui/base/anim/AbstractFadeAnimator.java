/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.07.15
 */
package com.netimen.ui.base.anim;

import android.view.View;

abstract class AbstractFadeAnimator {
    protected int lastDesiredVisibility = -1;
    protected int animationDuration;

    protected AbstractFadeAnimator() {
    }

    public AbstractFadeAnimator(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    public void setDuration(int duration) {
        animationDuration = duration;
    }

    public void animateVisibility(final View view, boolean show, final Runnable endAction) {
        animateVisibility(view, show, false, endAction);
    }

    /**
     * @param preventRestarting if true, doesn't restart if desired visibility is the same as last time
     */
    public void animateVisibility(final View view, final boolean show, boolean preventRestarting, final Runnable endAction) {
        int desiredVisility = show ? View.VISIBLE : View.INVISIBLE;
        if (preventRestarting && desiredVisility == lastDesiredVisibility)
            return;

        lastDesiredVisibility = desiredVisility;
        if (show) view.setVisibility(View.VISIBLE);

        performAnimateVisibility(new Anim(view, show, endAction));
    }

    protected void onAnimationEnd(Anim anim) {
        if (!anim.show)
            anim.view.setVisibility(View.INVISIBLE);
        lastDesiredVisibility = -1;

        if (anim.endAction != null)
            anim.endAction.run();
    }

    protected abstract void performAnimateVisibility(final Anim anim);

    protected class Anim {
        public final View view;
        public final boolean show;
        public final Runnable endAction;

        public Anim(View view, boolean show, Runnable endAction) {
            this.view = view;
            this.show = show;
            this.endAction = endAction;
        }
    }
}
