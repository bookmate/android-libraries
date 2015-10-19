/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   18.05.15
 */
package com.bookmate.libs.translateview;


import com.bookmate.libs.translateview.results.YandexDictionaryResult;

import retrofit.http.GET;
import retrofit.http.Query;

interface YandexDictionaryAPI {
    String API_KEY = "dict.1.1.20150518T080015Z.7436ad8de8acc2a7.e24bdd9c019936015f992d1bdc0731cca218ffa0";
    String ENDPOINT = "https://dictionary.yandex.net/api/v1/dicservice.json";
    int FLAGS_MORPHO = 4;

    /**
     * doc: https://tech.yandex.ru/dictionary/doc/dg/reference/lookup-docpage/
     * sample: https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20150518T080015Z.7436ad8de8acc2a7.e24bdd9c019936015f992d1bdc0731cca218ffa0&lang=en-ru&text=play&ui=ru&flags=4
     *
     * @param lang  "ru" - translate from auto-detected language to Russian. "en-ru" - translate from English to Russian
     * @param ui    "ru", "en" ... - language for the dictionary article
     * @param flags 4 - morphological search (doesn't find without this)
     */
    @GET("/lookup")
    YandexDictionaryResult lookup(
            @Query("text") String text,
            @Query("lang") String lang,
            @Query("flags") int flags,
            @Query("ui") String ui);
}
