/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.12.15
 */
package com.bookmate.libs.placeholders;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;

public class StateView extends TextView {

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(context, attrs);
    }

    public void show(String state) {
//        setText(getCaption(state));
//        showIcon(getIcon(state));
    }

    private void showIcon(int iconId) {
        setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
    }


    public static Params loadAttributes(Context context, AttributeSet attrs) {
        Params params = new Params();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateView);
        final int iconsArrayId = a.getResourceId(R.styleable.StateView_statesIcons, 0);
        TypedArray icons = context.getResources().obtainTypedArray(iconsArrayId);
        icons.recycle();
//        params.captionNoDataRes = a.getResourceId(R.styleable.EmptyView_captionNoData, DEFAULT_CAPTION_NO_DATA_RES);
//        params.captionErrorRes = a.getResourceId(R.styleable.EmptyView_captionError, DEFAULT_CAPTION_ERROR_RES);
//        params.iconErrorRes = a.getResourceId(R.styleable.EmptyView_iconError, DEFAULT_ICON_ERROR_RES);
//        params.iconNoDataRes = a.getResourceId(R.styleable.EmptyView_iconNoData, DEFAULT_ICON_NO_DATA_RES);
        a.recycle();
        return params;
    }

    static class Params {
        @StringRes
        public int[] captions;

        @StringRes
        public int[] icons[];
    }
}
