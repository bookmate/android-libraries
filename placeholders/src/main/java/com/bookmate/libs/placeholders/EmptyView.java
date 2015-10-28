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
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bookmate.libs.base.Utils;


public class EmptyView extends TextView {

    protected static final int DEFAULT_CAPTION_NO_DATA_RES = R.string.no_data;
    protected static final int DEFAULT_CAPTION_NETWORK_ERROR_RES = R.string.network_error;
    protected static final int DEFAULT_CAPTION_SERVER_ERROR_RES = R.string.server_error;
    protected static final int DEFAULT_ICON_NO_DATA_RES = android.R.drawable.ic_menu_info_details;
    protected static final int DEFAULT_ICON_NETWORK_ERROR_RES = android.R.drawable.ic_menu_rotate;

    private OnClickListener onRefreshClickListener;
    private Params params;

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        params = loadAttributes(getContext(), attrs);
    }

    private void setNetworkError(Exception exception) {
        setText(networkErrorLogic.isServerError(exception) ? params.captionServerErrorRes : params.captionNetworkErrorRes);
        showIcon(params.iconNetworkErrorRes);
        if (onRefreshClickListener != null)
            setOnClickListener(onRefreshClickListener);
    }

    private void setNoData() {
        if (params.iconNoDataRes != 0) {
            showIcon(params.iconNoDataRes);
            setText(null);
        } else
            setText(params.captionNoDataRes);
        setOnClickListener(null);
    }

    /**
     * tries to determine whether it's a network connection error or a server problem and display appropriate message
     */
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
        params.captionNoDataRes = a.getResourceId(R.styleable.EmptyView_captionNoData, DEFAULT_CAPTION_NO_DATA_RES);
        params.captionNetworkErrorRes = a.getResourceId(R.styleable.EmptyView_captionNetworkError, DEFAULT_CAPTION_NETWORK_ERROR_RES);
        params.captionServerErrorRes = a.getResourceId(R.styleable.EmptyView_captionServerError, DEFAULT_CAPTION_SERVER_ERROR_RES);
        params.iconNetworkErrorRes = a.getResourceId(R.styleable.EmptyView_iconNetworkError, DEFAULT_ICON_NETWORK_ERROR_RES);
        params.iconNoDataRes = a.getResourceId(R.styleable.EmptyView_iconNoData, DEFAULT_ICON_NO_DATA_RES);
        a.recycle();
        return params;
    }

    public static Params loadDefaultAttributes(Context context) {
        Params params = new Params();
        params.captionNoDataRes = Utils.getAttributeValue(context, R.attr.captionNoData, DEFAULT_CAPTION_NO_DATA_RES);
        params.captionNetworkErrorRes = Utils.getAttributeValue(context, R.attr.captionNetworkError, DEFAULT_CAPTION_NETWORK_ERROR_RES);
        params.captionServerErrorRes = Utils.getAttributeValue(context, R.attr.captionServerError, DEFAULT_CAPTION_SERVER_ERROR_RES);
        params.iconNetworkErrorRes = Utils.getAttributeValue(context, R.attr.iconNetworkError, DEFAULT_ICON_NETWORK_ERROR_RES);
        params.iconNoDataRes = Utils.getAttributeValue(context, R.attr.iconNoData, DEFAULT_ICON_NO_DATA_RES);
        return params;
    }

    public void setCaptionNoData(String errorMessage) {
        setText(errorMessage);
    }

    static class Params {
        @StringRes
        public int captionNoDataRes, captionNetworkErrorRes, captionServerErrorRes;

        @StringRes
        public int iconNetworkErrorRes, iconNoDataRes;
    }

    //region network error logic
    public static abstract class NetworkErrorLogic {

        public abstract boolean isServerError(Exception exception);
    }

    private static NetworkErrorLogic networkErrorLogic = new NetworkErrorLogic() {
        @Override
        public boolean isServerError(Exception exception) {
            return false;
        }
    };

    public static void setNetworkErrorLogic(NetworkErrorLogic networkErrorLogic) {
        EmptyView.networkErrorLogic = networkErrorLogic;
    }
    //endregion
}
