/**
 * Copyright (c) 2014 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   26.06.14
 */
package com.bookmate.libs.placeholders;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.bookmate.libs.base.anim.FadeAnimator;

public class LoaderStateView extends FrameLayout {

    LoadingView loadingView;
    StateView stateView;

    FadeAnimator fadeAnimator;

    public LoaderStateView(Context context) {
        super(context);
        create(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    public LoaderStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoaderStateView);
        int animationDuration = a.getInt(R.styleable.LoaderStateView_animationDuration, getResources().getInteger(android.R.integer.config_mediumAnimTime));
        a.recycle();
        create(animationDuration);
//        stateView.setParams(StateView.loadAttributes(getContext(), attrs)); // passing attributes to StateView
    }

    protected void create(int animationDuration) {
        fadeAnimator = new FadeAnimator(animationDuration);

        inflate(getContext(), R.layout.view_loader, this);
        loadingView = (LoadingView) findViewById(R.id.loader_view_loading);
//        stateView = (StateView) findViewById(R.id.loader_view_empty);
    }

    public void showNoData(int noDataTextRes) {
//        stateView.getParams().captionNoDataRes = noDataTextRes;
//        showNoData();
    }

    public void hide() {
        hide(false);
    }

    public void hide(boolean animate) {
//        state = null;
//        uiThreadHelper.hideOnUiThread(animate);
    }

    public void setLoadingMode() {
        loadingView.setLoadingMode(LoadingView.Mode.TEXT);
    }

    public void setLoadingTextColor(int textColor) {
        loadingView.setLoadingTextColor(textColor);
    }

    public void showState(int state) {
        setVisibility(VISIBLE);
//        setState(state);
    }

}
