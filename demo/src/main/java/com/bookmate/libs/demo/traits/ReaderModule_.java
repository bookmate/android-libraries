/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.08.15
 */
package com.bookmate.libs.demo.traits;

import com.bookmate.libs.traits.BusModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ReaderModule_ extends BusModule {

    @Singleton
    @Provides
    ReaderSharedFields_ readerSharedFields() {
        return new ReaderSharedFields_();
    }
}
