/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   21.05.15
 */
package com.bookmate.libs.translateview.results;

import android.text.TextUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import java.util.Arrays;

public class YandexDictionaryResult extends YandexResult {
    Definition def[];

    @Override
    public boolean isSuccessfull() {
        return super.isSuccessfull() && def != null && def.length > 0;
    }

    /**
     * list of supported tags: http://daniel-codes.blogspot.com/2011/04/html-in-textviews.html
     */
    public String html() {
        return join(joiner("<br><br>"), def);
    }

    class Definition {
        String text, pos, ts;
        Translation tr[];

        @Override
        public String toString() { // needed for join();
            final String position = lightFont(decorate(pos, "<small><i>", "</i></small>"), true);
            final String tsFormatted = lightFont(decorate(ts, "[", "]"), true);
            return newLine().join(spaces().join(text, tsFormatted), position, decorateArray(tr, new Function<Translation, String>() {
                public int number;

                @Override
                public String apply(Translation input) {
                    return tr.length > 1 ? ++number + " " + input.toString() : input.toString(); // adding number if > 1 translation
                }
            }));
        }
    }

    class Translation {
        String text;
        Synonym[] syn;
        Text[] mean;
        Example[] ex;

        @Override
        public String toString() { // needed for join();
            final String translation = comma().join(text, join(comma(), syn)); // text + synonyms
            final String meaning = indent(lightFont(decorate(join(comma(), mean), "(", ")"), false)); // (meaning)
            return newLine().join(translation, meaning, join(newLine(), ex)); // examples on new line each
        }

    }

    class Synonym {
        String text;

        @Override
        public String toString() { // needed for join();
            return text;
        }
    }

    /**
     * needed for {'text':'lalala'} objects
     */
    class Text {
        String text;

        @Override
        public String toString() { // needed for join();
            return text;
        }
    }

    class Example {
        String text;
        Text tr[];

        @Override
        public String toString() { // needed for join();
            return lightFont(indent(joiner(" — ").join(text, join(comma(), tr))), false); // play games - играть в игры, играть в игру
        }

    }

    /// helper methods

    private static Joiner joiner(String separator) {
        return Joiner.on(separator).skipNulls();
    }

    private static Joiner newLine() {
        return joiner("<br>");
    }

    private static Joiner comma() {
        return joiner(", ");
    }

    private static Joiner spaces() {
        return joiner(" ");
    }

    private static String join(Joiner joiner, Object[] objects) {
        return (objects == null || objects.length == 0) ? null : joiner.join(objects);
    }

    /**
     * @return null if {@param s} is null, {@param prefix} + {@param s} + {@param suffix} otherwise
     */
    private static String decorate(CharSequence s, String prefix, String suffix) {
        return TextUtils.isEmpty(s) ? null : prefix + s + suffix;
    }

    private static String lightFont(CharSequence s, boolean lightFace) {
        return decorate(s, "<font" + (lightFace ? " face='sans-serif-light'" : "") + " color='#BCAA99'>", "</font>");
    }

    private static String indent(String s) {
//        return decorate(s, "&nbsp;&nbsp;&nbsp;&nbsp;", "");
        return decorate(s, "", "");
    }

    private static <T> String decorateArray(T[] array, Function<T, String> function) {
        return array == null ? null : newLine().join(Iterables.transform(Arrays.asList(array), function));
    }
}
