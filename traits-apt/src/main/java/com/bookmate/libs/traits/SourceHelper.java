/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.03.15
 */
package com.bookmate.libs.traits;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Needed to guess class package by name. The problem is that when we write @Event void onMyEvent(), the event class name is MyEvent, but we can't know the package.
 * This class tries to guess the sources dir by the manifest file name and then parses that directory looking for java classes and guesses the package name by path. It works pretty fast (indexing ~500 classes took less than a second).
 */
class SourceHelper {
    private Map<String, TypeElement> classNameToTypeElement = new HashMap<>();

    /**
     * this helper accumulates classes from each processing round http://hannesdorfmann.com/annotation-processing/annotationprocessing101/#processing-rounds
     */
    public void buildClassesMap(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        final long startTime = System.currentTimeMillis();
        for (Element element : roundEnv.getRootElements())
            classNameToTypeElement.put(element.getSimpleName().toString(), (TypeElement) element);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Traits source helper processed " + classNameToTypeElement.size() + " classes in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public TypeElement getTypeElement(String simpleName) {
        return classNameToTypeElement.get(simpleName);
    }
}
