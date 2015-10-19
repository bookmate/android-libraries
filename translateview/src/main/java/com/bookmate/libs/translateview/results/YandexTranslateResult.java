/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.04.15
 */
package com.bookmate.libs.translateview.results;

@SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
public class YandexTranslateResult extends YandexResult {

    /**
     * for some reason Yandex returns array of strings here
     */
    private String[] text;

    private String lang;

    public String[] getTranslation() {
        return text;
    }

    public String getLang() {
        return lang;
    }
}
