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
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.bookmate.libs.base.anim.FadeAnimator;

public class LoaderView extends FrameLayout {

    LoadingView loadingView;
    EmptyView emptyView;

    FadeAnimator fadeAnimator;
    private final LoaderViewUiThreadHelper uiThreadHelper = new LoaderViewUiThreadHelper(this);
    private State state;

    public LoaderView(Context context) {
        super(context);
        create(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    public LoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoaderView);
        int animationDuration = a.getInt(R.styleable.LoaderView_animationDuration, getResources().getInteger(android.R.integer.config_mediumAnimTime));
        a.recycle();
        create(animationDuration);
    }

    protected void create(int animationDuration) {
        fadeAnimator = new FadeAnimator(animationDuration);

        inflate(getContext(), R.layout.view_loader, this);
        loadingView = (LoadingView) findViewById(R.id.loader_view_loading);
        emptyView = (EmptyView) findViewById(R.id.loader_view_empty);
    }

    public void setOnRefreshClickListener(OnClickListener onClickListener) {
        emptyView.setOnRefreshClickListener(onClickListener);
    }

    public void showLoading() {
        state = State.LOADING;
        uiThreadHelper.showLoadingOnUiThread();
    }

    public void showNetworkError(@NonNull Exception exception) {
        if (state == State.NETWORK_ERROR)
            uiThreadHelper.showToastOnUiThread(emptyView.getParams().captionNetworkErrorRes);
        else {
            state = State.NETWORK_ERROR;
            uiThreadHelper.showNetworkErrorOnUiThread(exception);
        }
    }

    public void showNoData(int noDataTextRes) {
        emptyView.getParams().captionNoDataRes = noDataTextRes;
        showNoData();
    }

    public void showNoDataIcon(int iconRes) {
        emptyView.getParams().iconNoDataRes = iconRes;
        showNoData();
    }

    public void showNoData() {
        state = State.NO_DATA;
        uiThreadHelper.showNoDataOnUiThread();
    }

    public void hide() {
        hide(false);
    }

    public void hide(boolean animate) {
        state = null;
        uiThreadHelper.hideOnUiThread(animate);
    }

    /**
     * @return true is in loading state
     */
    public boolean isStateLoading() {
        return state == State.LOADING;
    }

    public void showLoadingText() {
        loadingView.setLoadingMode(LoadingView.Mode.TEXT);
    }

    public void setLoadingTextColor(int textColor) {
        loadingView.setLoadingTextColor(textColor);
    }

    public void setCaptionNoData(int captionNoDataRes) {
        emptyView.setCaptionNoData(captionNoDataRes);
    }

    public void setIconNoData(int iconNoDataRes) {
        emptyView.setIconNoData(iconNoDataRes);
    }

    public void setCaptionNoData(String errorMessage) {
        emptyView.setCaptionNoData(errorMessage);
    }

    public void showLoading(boolean show) {
        if (show)
            showLoading();
        else
            hide();
    }

    enum State {
        NETWORK_ERROR, NO_DATA, LOADING
    }
}
