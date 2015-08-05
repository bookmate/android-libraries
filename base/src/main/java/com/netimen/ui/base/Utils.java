/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   30.07.15
 */
package com.netimen.ui.base;

import android.content.Context;

public class Utils {
    public static float px2dp(Context context, float pixels) {
        return pixels / (context.getResources().getDisplayMetrics().densityDpi / 160.f);
    }
}
