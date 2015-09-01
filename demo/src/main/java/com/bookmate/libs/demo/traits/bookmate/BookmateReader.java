/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   19.08.15
 */
package com.bookmate.libs.demo.traits.bookmate;

import com.bookmate.libs.demo.traits.readercode.Document;

import java.io.Serializable;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class BookmateReader implements Serializable { // CUR generate
    private final Document document;
    private final String bookmark, discovered;

    public BookmateReader(Document document, String bookmark, String discovered) {
        this.document = document;
        this.bookmark = bookmark;
        this.discovered = discovered;
    }

    @Provides
    Document document() {
        return document;
    }

    @Provides
    @Named("bookmark")
    String bookmark() {
        return bookmark;
    }

    @Provides
    @Named("discovered")
    String discovered() {
        return discovered;
    }
}
