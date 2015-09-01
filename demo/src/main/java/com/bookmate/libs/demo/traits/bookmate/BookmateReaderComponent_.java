/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   19.08.15
 */
package com.bookmate.libs.demo.traits.bookmate;

import com.bookmate.libs.demo.traits.ReaderModule_;
import com.bookmate.libs.demo.traits.ReaderTrait;
import com.bookmate.libs.traits.BusModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {BookmateReader.class, ReaderModule_.class, BusModule.class})
public interface BookmateReaderComponent_ {

    void inject(BookmateReaderTrait trait);

    void inject(ReaderTrait trait);
}
