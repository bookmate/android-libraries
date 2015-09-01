/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   19.08.15
 */
package com.bookmate.libs.demo.traits.bookmate;


import com.bookmate.libs.demo.traits.ReaderTrait;
import com.bookmate.libs.demo.traits.readercode.Document;
import com.bookmate.libs.traits.Trait;

import javax.inject.Inject;
import javax.inject.Named;

@Trait(BookmateReader.class)
public abstract class BookmateReaderTrait extends ReaderTrait {

    @Inject
    Document document;

    @Inject
    @Named("bookmark")
    String bookmark;

    @Inject
    @Named("discovered")
    String discovered;
}
