/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   30.07.15
 */
package com.bookmate.libs.base;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.util.TypedValue;

public class Utils {
    public static float px2dp(Context context, float pixels) {
        return pixels / (context.getResources().getDisplayMetrics().densityDpi / 160.f);
    }

    public static int getAttributeValue(Context context, @AttrRes int attr, int defaultValue) {
        TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, value, false))
            return value.data;
        return defaultValue;
    }
}
