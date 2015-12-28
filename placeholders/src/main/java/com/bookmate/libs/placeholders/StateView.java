/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.12.15
 */
package com.bookmate.libs.placeholders;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class StateView extends TextView{

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void show(String state) {
        setText(getCaption(state));
        showIcon(getIcon(state));
    }

    private void showIcon(int iconId) {
        setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
    }

}
