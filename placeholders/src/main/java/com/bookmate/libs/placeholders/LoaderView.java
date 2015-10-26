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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bookmate.libs.base.anim.FadeAnimator;

public class LoaderView extends FrameLayout {

    private final FadeAnimator fadeAnimator;
    LoadingView loadingView;

    private EmptyView emptyView;
    private State state;

//    public LoaderView(Context context) { // cur
//        super(context);
//    }

    public LoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoaderView);
        @LayoutRes int emptyViewResId = a.getResourceId(R.styleable.LoaderView_layoutEmptyView, R.layout.view_empty);
        int animationDuration = a.getInt(R.styleable.LoaderView_animationDuration, getResources().getInteger(android.R.integer.config_mediumAnimTime));
        a.recycle();
        fadeAnimator = new FadeAnimator(animationDuration);

        loadingView = new LoadingView(getContext(), attrs);
        addView(loadingView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        inflate(getContext(), emptyViewResId, this);
        emptyView = (EmptyView) getChildAt(getChildCount() - 1);
    }

    public void setOnRefreshClickListener(OnClickListener onClickListener) {
        emptyView.setOnRefreshClickListener(onClickListener);
    }

    //    @UiThread(propagation = UiThread.Propagation.REUSE) // cur
    public void showLoading() {
        state = State.LOADING;
        setVisibility(View.VISIBLE);
        emptyView.setVisibility(GONE);
        loadingView.setVisibility(VISIBLE);
    }

    //    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void showNetworkError(@NonNull Exception exception) {
        if (state == State.NETWORK_ERROR)
            Toast.makeText(getContext(), emptyView.params.captionNetworkErrorRes, Toast.LENGTH_SHORT).show();
        else {
            state = State.NETWORK_ERROR;
            loadingView.setVisibility(GONE);
            emptyView.showNetworkError(exception);
        }
    }

    //    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void showNoData(int noDataTextRes) {
        state = State.NO_DATA;
        emptyView.getParams().captionNoDataRes = noDataTextRes;
        showNoData();
    }

    //    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void showNoDataIcon(int iconRes) {
        emptyView.getParams().iconNoDataRes = iconRes;
        showNoData();
    }

    //    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void showNoData() {
        setVisibility(VISIBLE);
        loadingView.setVisibility(GONE);
        emptyView.showNoData();
    }

    public void hide() {
        state = null;
        setVisibility(GONE);
    }

    public void hide(boolean animate) {
        if (animate) {
            fadeAnimator.animateVisibility(this, false);
        } else
            hide();
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
