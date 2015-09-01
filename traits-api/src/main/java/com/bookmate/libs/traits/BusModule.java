/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   19.08.15
 */
package com.bookmate.libs.traits;

//@Module
public class BusModule {

//    @Provides
//    @Singleton
    Bus bus() {
        return new Bus();
    }

}
