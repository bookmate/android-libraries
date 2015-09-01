package com.bookmate.libs.traits;

/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.08.15
 */
public @interface Trait {
    Class<?> value() default Object.class;
}
