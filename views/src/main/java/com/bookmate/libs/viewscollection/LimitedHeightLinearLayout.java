/**
 * Copyright (c) 2014 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   27.08.14
 */
package com.bookmate.libs.viewscollection;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class LimitedHeightLinearLayout extends LinearLayout {

    private float maxHeight;

    public LimitedHeightLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LimitedHeightView);
        maxHeight = a.getDimension(R.styleable.LimitedHeightView_maxHeight, -1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        if (maxHeight > 0 && maxHeight < measuredHeight) {
            int measureMode = View.MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) maxHeight, measureMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        invalidate();
    }
}