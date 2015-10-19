/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   29.04.15
 */
package com.bookmate.libs.translateview;


import com.bookmate.libs.translateview.results.YandexLanguagesResult;
import com.bookmate.libs.translateview.results.YandexTranslateResult;

import retrofit.http.GET;
import retrofit.http.Query;

interface YandexTranslateAPI {
    String API_KEY = "trnsl.1.1.20150429T094048Z.cf5c9653c32cd8d3.49b3cdf2bd92a2b8920c71b160469e61c3c374d0";
    String ENDPOINT = "https://translate.yandex.net/api/v1.5/tr.json";

    /**
     * doc: https://tech.yandex.ru/translate/doc/dg/reference/translate-docpage/
     * sample: https://translate.yandex.net/api/v1.5/tr.json/translate?text=hello%2C+world%21&lang=ru&key=trnsl.1.1.20150429T094048Z.cf5c9653c32cd8d3.49b3cdf2bd92a2b8920c71b160469e61c3c374d0
     *
     * @param lang "ru" - translate from auto-detected language to Russian. "en-ru" - translate from English to Russian
     */
    @GET("/translate")
    YandexTranslateResult translate(
            @Query("text") String text,
            @Query("lang") String lang);


    /**
     * doc: https://tech.yandex.ru/translate/doc/dg/reference/getLangs-docpage/
     * sample: https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20150429T094048Z.cf5c9653c32cd8d3.49b3cdf2bd92a2b8920c71b160469e61c3c374d0
     * <p/>
     * key - API-ключ. Выдается бесплатно.
     * ui - Если задан, ответ будет дополнен расшифровкой кодов языков. Названия языков будут выведены на языке, код которого соответствует этому параметру.
     * Возможные значения: ru, en, uk, be, ... .
     */
    @GET("/getLangs")
    YandexLanguagesResult getLanguages(
            @Query("ui") String ui
    );
}
