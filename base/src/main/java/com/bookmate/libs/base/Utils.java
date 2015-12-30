/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   30.07.15
 */
package com.bookmate.libs.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

    /**
     * @param resourceType "drawable", "string" etc
     * @param attrId       R.styleable.SomeView_someAttr
     * @param attrName     human-readable attr name for displaying errors
     */
    public static int[] loadResourceIds(@NonNull TypedArray a, @NonNull String resourceType, int attrId, String attrName) {
        final int resourcesArrayId = a.getResourceId(attrId, -1);
        if (resourcesArrayId == -1)
            return null;

        TypedArray resources = a.getResources().obtainTypedArray(resourcesArrayId);
        int[] resourceIds = new int[resources.length()];
        for (int i = 0; i < resources.length(); i++) {
            if (!resources.hasValue(i)) // if position is set to @null
                continue;

            resourceIds[i] = resources.getResourceId(i, -1);
            if (resourceIds[i] == -1 || !TextUtils.equals(resourceType, a.getResources().getResourceTypeName(resourceIds[i])))
                throw new IllegalArgumentException("Can't parse " + attrName + ": incorrect " + resourceType + " at position " + i);
        }

        resources.recycle();
        return resourceIds;
    }
}
