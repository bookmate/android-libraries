/**
 * Copyright (c) 2014 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   25.06.14
 */
package com.bookmate.libs.placeholders;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class EmptyView extends TextView {

    private OnClickListener onRefreshClickListener;
    private Params params;

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        params = loadAttributes(getContext(), attrs);
    }

    /**
     * now logic is simple: show "no internet" if we had NoConnectivityException, "server error" otherwise.
     */
    private void setNetworkError(Exception exception) {
        setText(isNetworkProblem(exception) ? params.captionNetworkErrorRes : R.string.server_error);
        showIcon(params.iconNetworkErrorRes);
        if (onRefreshClickListener != null)
            setOnClickListener(onRefreshClickListener);
    }

    private boolean isNetworkProblem(Exception exception) {
        boolean networkProblem = exception instanceof NoNetworkException
                || exception.getCause() instanceof ConnectivityAwareClient.NoConnectivityException
                || (exception.getCause() != null && (exception.getCause().getCause() instanceof SocketTimeoutException
                || exception.getCause().getCause() instanceof UnknownHostException));
        if (!networkProblem) {
            Crashlytics.logException(exception);
        }
        return networkProblem;
    }

    private void setNoData() {
        if (params.iconNoDataRes != 0)
            showIcon(params.iconNoDataRes);
        else
            setText(params.captionNoDataRes);
        setOnClickListener(null);
    }

    public void showNetworkError(Exception exception) {
        setVisibility(View.VISIBLE);
        setNetworkError(exception);
    }

    public void showNoData() {
        setVisibility(View.VISIBLE);
        setNoData();
    }

    public Params getParams() {
        return params;
    }

    public void setCaptionNoData(int captionNoDataRes) {
        this.params.captionNoDataRes = captionNoDataRes;
    }

    public void setIconNoData(int iconNoDataRes) {
        this.params.iconNoDataRes = iconNoDataRes;
    }

    public void setOnRefreshClickListener(OnClickListener onClickListener) {
        this.onRefreshClickListener = onClickListener;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    private void showIcon(int iconId) {
        setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
    }

    public static Params loadAttributes(Context context, AttributeSet attrs) {
        Params params = new Params();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyView);
        params.captionNetworkErrorRes = a.getResourceId(R.styleable.EmptyView_captionNetworkError, R.string.text_no_network);
        params.captionNoDataRes = a.getResourceId(R.styleable.EmptyView_captionNoData, R.string.text_no_data);
        params.iconNetworkErrorRes = a.getResourceId(R.styleable.EmptyView_iconNetworkError, R.drawable.ic_refresh);
        params.iconNoDataRes = a.getResourceId(R.styleable.EmptyView_iconNoData, 0);
        a.recycle();
        return params;
    }

    public static Params loadDefaultAttributes() {
        Params params = new Params();
        params.captionNetworkErrorRes = R.string.text_no_network;
        params.captionNoDataRes = R.string.text_no_data;
        params.iconNetworkErrorRes = R.drawable.ic_refresh;
        params.iconNoDataRes = 0;
        return params;
    }

    public void setCaptionNoData(String errorMessage) {
        setText(errorMessage);
    }

    static class Params {
        public int iconNetworkErrorRes;
        public int iconNoDataRes;
        public int captionNoDataRes;
        public int captionNetworkErrorRes;
    }
}
