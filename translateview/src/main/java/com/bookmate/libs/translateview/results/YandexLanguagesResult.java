/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.04.15
 */
package com.bookmate.libs.translateview.results;

import java.util.Map;

public class YandexLanguagesResult extends YandexResult {
    String[] dirs;

    private Map<String, String> langs;

    public String[] getDirs() {
        return dirs;
    }

    public Map<String, String> getLangs() {
        return langs;
    }
}
