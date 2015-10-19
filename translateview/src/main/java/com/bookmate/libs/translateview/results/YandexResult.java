/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   22.05.15
 */
package com.bookmate.libs.translateview.results;

public class YandexResult {
    public static final int RESULT_CODE_DIRECTION_NOT_SUPPORTED = 501;
    static final int RESULT_CODE_SUCCESS = 200;

    public int code;

    public boolean isSuccessfull() {
        return code == YandexResult.RESULT_CODE_SUCCESS || code == 0; // in some cases there is no code after successful requests, so 0 is also valid
    }

}
