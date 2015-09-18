/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   18.09.15
 */
package com.bookmate.libs.traits;

import javax.lang.model.element.ExecutableElement;

public class Utils {
    public static String methodName(ExecutableElement element) {
        return element.getSimpleName().toString();
    }
}
