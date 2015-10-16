/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.05.15
 */
package com.bookmate.libs.androidviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Animates text changes: old text fades, width animates, new text appears
 */
public class FadeChangeTextView extends TextView {
    private static final String LOG_TAG = FadeChangeTextView.class.getSimpleName();

    int animDuration;
    private boolean isAnimating;

    public FadeChangeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FadeChangeTextView);
        animDuration = a.getInt(R.styleable.FadeChangeTextView_animDuration, getResources().getInteger(android.R.integer.config_mediumAnimTime));
        a.recycle();
    }

    @Override
    public void setText(@Nullable final CharSequence text, final BufferType type) { // setText(text) is final, so overriding this method instead
        if (TextUtils.equals(text, getText()) || TextUtils.isEmpty(getText()) || isAnimating) { // avoiding re-animating same text and first time no animation
            super.setText(text, type); // crashes without this
            return;
        }

        Log.d(LOG_TAG, "setText this: " + this + " text: " + text);

        isAnimating = true;

        animateColorAlpha(0, new Runnable() {
            @Override
            public void run() {
                final ValueAnimator widthAnimator = ValueAnimator.ofObject(new IntEvaluator() { // there is no easy way to animate the width http://stackoverflow.com/a/9090116/190148
                    @NonNull
                    @Override
                    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                        int currentWidth = super.evaluate(fraction, startValue, endValue);
                        ViewGroup.LayoutParams params = getLayoutParams();
                        params.width = currentWidth;
                        setLayoutParams(params);
                        return currentWidth;
                    }
                }, getWidth(), (int) getPaint().measureText(TextUtils.isEmpty(text) ? " " : text.toString()) + getPaddingLeft() + getPaddingRight()).setDuration(animDuration);

                widthAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        FadeChangeTextView.super.setText(text, type);
                        animateColorAlpha(0xFF, new Runnable() {
                            @Override
                            public void run() {
                                isAnimating = false;
                            }
                        });
                    }
                });

                widthAnimator.start();
            }
        });
    }

    private void animateColorAlpha(int desiredAlpha, final Runnable endAction) {
        final int currentColor = getCurrentTextColor(), desiredColor = (currentColor & 0x00FFFFFF) | desiredAlpha << 24; // same color bits and new alpha bits
        final ObjectAnimator animator = ObjectAnimator.ofInt(this, "textColor", currentColor, desiredColor).setDuration(animDuration);
        animator.setEvaluator(new IntEvaluator() { // we need to change only the alpha-bits, or we get some strange color animation
            @NonNull
            @Override
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                int startAlpha = (startValue & 0xFF000000) >>> 24, endAlpha = (endValue & 0xFF000000) >>> 24, curAlpha = (int) (startAlpha + (endAlpha - startAlpha) * fraction);
                return (startValue & 0x00FFFFFF) | (curAlpha << 24); // combining color bits and new alpha bits
            }
        });
        if (endAction != null)
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    endAction.run();
                }
            });
        animator.start();
    }

}
