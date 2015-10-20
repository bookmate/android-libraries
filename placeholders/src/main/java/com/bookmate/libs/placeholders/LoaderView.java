/**
 * Copyright (c) 2014 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   26.06.14
 */
package com.bookmate.libs.placeholders;


//@EViewGroup(R.layout.layout_loader)
//public class LoaderView extends FrameLayout {
//
//    private static final int EMPTY_VIEW_CHILD_INDEX = 1;
//
//    @ViewById(android.R.id.empty)
//    EmptyView emptyViewBasic;
//
//    @ViewById(R.id.loading_view)
//    LoadingView loadingView;
//
//    @IntegerRes(R.integer.animation_fast)
//    int SHORT_ANIMATION_DURATION;
//
//    private final EmptyView.Params params;
//    private EmptyView emptyView;
//    private Animation animationFadeOut;
//    private State state;
//
//    public LoaderView(Context context) {
//        super(context);
//        params = EmptyView.loadDefaultAttributes();
//        setupAnimation();
//    }
//
//    public LoaderView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        params = EmptyView.loadAttributes(getContext(), attrs);
//        setupAnimation();
//    }
//
//    @AfterViews
//    void ready() {
//        setEmptyView(emptyViewBasic);
//        emptyView.setParams(params);
//    }
//
//    @SuppressWarnings("WeakerAccess")
//    public void setEmptyView(EmptyView emptyView) {
//        this.emptyView = emptyView;
//
//        final View currentEmptyView = getChildAt(EMPTY_VIEW_CHILD_INDEX);
//        if (currentEmptyView != emptyView) {
//            removeView(currentEmptyView);
//            addView(emptyView, currentEmptyView.getLayoutParams());
//        }
//    }
//
//    public void setOnRefreshClickListener(OnClickListener onClickListener) {
//        emptyView.setOnRefreshClickListener(onClickListener);
//    }
//
//    @UiThread(propagation = UiThread.Propagation.REUSE)
//    public void showLoading() {
//        state = State.LOADING;
//        setVisibility(View.VISIBLE);
//        emptyView.setVisibility(GONE);
//        loadingView.setVisibility(VISIBLE);
//    }
//
//    @UiThread(propagation = UiThread.Propagation.REUSE)
//    public void showNetworkError(@NonNull Exception exception) {
//        if (state == State.NETWORK_ERROR)
//            Toast.makeText(getContext(), R.string.text_no_network, Toast.LENGTH_SHORT).show();
//        else {
//            state = State.NETWORK_ERROR;
//            loadingView.setVisibility(GONE);
//            emptyView.showNetworkError(exception);
//        }
//    }
//
//    @UiThread(propagation = UiThread.Propagation.REUSE)
//    public void showNoData(int noDataTextRes) {
//        state = State.NO_DATA;
//        emptyView.getParams().captionNoDataRes = noDataTextRes;
//        showNoData();
//    }
//
//    @UiThread(propagation = UiThread.Propagation.REUSE)
//    public void showNoDataIcon(int iconRes) {
//        emptyView.getParams().iconNoDataRes = iconRes;
//        showNoData();
//    }
//
//    @UiThread(propagation = UiThread.Propagation.REUSE)
//    public void showNoData() {
//        setVisibility(VISIBLE);
//        loadingView.setVisibility(GONE);
//        emptyView.showNoData();
//    }
//
//    public void hide() {
//        state = null;
//        setVisibility(GONE);
//    }
//
//    @SuppressWarnings("SameParameterValue")
//    public void hide(boolean animate) {
//        if (animate) {
//            startAnimation(animationFadeOut);
//        } else
//            hide();
//    }
//
//    /**
//     * @return true is in loading state
//     */
//    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
//    public boolean isStateLoading() {
//        return state == State.LOADING;
//    }
//
//    private void setupAnimation() {
//        animationFadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
//        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                setVisibility(View.GONE);
//            }
//        };
//        animationFadeOut.setAnimationListener(animationListener);
//        animationFadeOut.setDuration(SHORT_ANIMATION_DURATION);
//    }
//
//    public void showLoadingText() {
//        loadingView.setLoadingMode(LoadingView.Mode.TEXT);
//    }
//
//    public void setLoadingTextColor(int textColor) {
//        loadingView.setLoadingTextColor(textColor);
//    }
//
//    @SuppressWarnings("SameParameterValue")
//    public void setCaptionNoData(int captionNoDataRes) {
//        emptyView.setCaptionNoData(captionNoDataRes);
//    }
//
//    public void setIconNoData(int iconNoDataRes) {
//        emptyView.setIconNoData(iconNoDataRes);
//    }
//
//    public void setCaptionNoData(String errorMessage) {
//        emptyView.setCaptionNoData(errorMessage);
//    }
//
//    public void showLoading(boolean show) {
//        if (show)
//            showLoading();
//        else
//            hide();
//    }
//
//    enum State {
//        NETWORK_ERROR, NO_DATA, LOADING
//    }
//}
