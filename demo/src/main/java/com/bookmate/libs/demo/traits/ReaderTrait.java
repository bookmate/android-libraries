/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   18.08.15
 */
package com.bookmate.libs.demo.traits;


import com.bookmate.libs.demo.traits.readercode.ReadingSystem;
import com.bookmate.libs.traits.Bus;
import com.bookmate.libs.traits.Trait;

import javax.inject.Inject;


@Trait(ReaderSharedFields_.class)
public abstract class ReaderTrait { // CUR generate?

    @Inject
    protected Bus bus;

    @Inject
    protected ReaderSharedFields_ readerSharedFields;

    protected ReadingSystem readingSystem() {
        return readerSharedFields.readingSystem;
    }

    protected void setReadingSystem(ReadingSystem readingSystem) {
        readerSharedFields.readingSystem = readingSystem;
    }

}
