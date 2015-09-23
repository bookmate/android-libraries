/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.09.15
 */
package com.bookmate.libs.traits;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores some data needed to build the helper class (constructor builder, type builder etc)
 */
class HelperClassBuilder {
    public static final String TRAIT_FIELD_NAME = "trait";
    public static final String ACCESS_BUS = TRAIT_FIELD_NAME + ".bus";

    final TypeSpec.Builder classBuilder;
    final MethodSpec.Builder constructorBuilder;
    /**
     * how many listeners to this event we already generated in this class
     */
    final Map<String, Integer> listenersCountMap = new HashMap<>();

    public HelperClassBuilder(TypeSpec.Builder classBuilder, MethodSpec.Builder constructorBuilder) {
        this.classBuilder = classBuilder;
        this.constructorBuilder = constructorBuilder;
    }

    public String getListenersCount(String eventOrRequestClassName) {
        Integer listenersCount = listenersCountMap.get(eventOrRequestClassName);
        if (listenersCount == null)
            listenersCount = 0;
        listenersCountMap.put(eventOrRequestClassName, listenersCount + 1);
        return listenersCount > 0 ? String.valueOf(listenersCount) : "";
    }

    public TypeSpec buildClass() {
        return classBuilder.addMethod(constructorBuilder.build()).build();
    }
}
