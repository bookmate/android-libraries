/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 *
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   13.03.15
 */
package com.bookmate.traits;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to subscribe to a request.
 * The difference between request and event is that event is used only to notify about something, and request is used to get some data from other submodule.
 *
 * <pre>
 * &#064;EBean
 * public class Submodule {
 *
 * 	&#064;Request
 * 	Integer getId() { // subscribes to GetId request, request name is inferred from method name
 * 	    return ...;
 * 	}
 *
 * 	&#064;Request
 * 	String handleGetAuthor(GetAuthor request) { // subscribes to ThirdEvent event, event name is inferred from parameter; method name doesn't matter
 * 	    return ...;
 * 	}
 *
 *
 * 	&#064;Request(IsActive.class)
 * 	boolean isActive() {
 * 	    return ...;
 * 	}
 * }
 * </pre>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@SuppressWarnings("UnusedDeclaration")
public @interface Request {
    Class<?> value() default Object.class;
    String[] moduleName() default "";
    Class<?>[] moduleClass() default Object.class;
}
