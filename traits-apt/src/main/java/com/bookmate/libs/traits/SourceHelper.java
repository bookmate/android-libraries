/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.03.15
 */
package com.bookmate.libs.traits;

import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;

/**
 * Needed to guess class package by name. The problem is that when we write @Event void onMyEvent(), the event class name is MyEvent, but we can't know the package.
 * This class tries to guess the sources dir by the manifest file name and then parses that directory looking for java classes and guesses the package name by path. It works pretty fast (indexing ~500 classes took less than a second).
 */
class SourceHelper {
    private Map<String, TypeElement> classNameToTypeElement = new HashMap<>();

    //region class simple name -> TypeElemenet
    /**
     * Accumulates classes from each processing round http://hannesdorfmann.com/annotation-processing/annotationprocessing101/#processing-rounds
     * This will be later needed for guessing event/request class by method name
     */
    public void buildSourceClassesMap(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        final long startTime = System.currentTimeMillis();
        for (Element element : roundEnv.getRootElements())
            classNameToTypeElement.put(element.getSimpleName().toString(), (TypeElement) element);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Traits source helper processed " + classNameToTypeElement.size() + " classes in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public TypeElement getTypeElement(String classSimpleName) {
        return classNameToTypeElement.get(classSimpleName);
    }
    //endregion

    //region get event or request ClassName
    /**
     * Tries to extract event or request class info from annotation parameter, method parameter or method name
     *
     * @return a {@link ClassName} object corresponding to the event class
     */
    public ClassName getEventOrRequestClassName(ExecutableElement methodElement) {
        try {
            Class<?> eventOrRequestClass = getEventOrRequestClassFromAnnotation(methodElement);

            if (eventOrRequestClass != Object.class) // Object is default value of annotation parameter, so we check, whether the parameter was explicitly set.
                return ClassName.get(eventOrRequestClass);
        } catch (MirroredTypeException mte) { // http://hannesdorfmann.com/annotation-processing/annotationprocessing101#datamodel
            TypeElement classTypeElement = (TypeElement) ((DeclaredType) mte.getTypeMirror()).asElement();
            if (!Object.class.getCanonicalName().equals(classTypeElement.getQualifiedName().toString())) // Object is default value of annotation parameter, so we check, whether the parameter was explicitly set.
                return ClassName.get(classTypeElement);
        }

        if (methodElement.getParameters().size() > 0)
            return (ClassName) ClassName.get(methodElement.getParameters().get(0).asType()); // CUR check types here List<String>

        return getClassNameByMethodName(methodElement);
    }

    /**
     * pageShown -> PageShown, onPageShown -> PageShown etc
     */
    public ClassName getClassNameByMethodName(ExecutableElement methodElement) {
        String className = Utils.extractMethodName(methodElement); // isPublic
        className = className.startsWith("on") ? className.substring(2) : className;
        className = Utils.toUpperCaseFirstCharacter(className);

        final TypeElement eventOrRequestTypeElement = getTypeElement(className);
        if (eventOrRequestTypeElement != null)
            return ClassName.get(eventOrRequestTypeElement);

        return null;
    }

    public static Class<?> getEventOrRequestClassFromAnnotation(ExecutableElement methodElement) throws MirroredTypeException {
        final Event annotation = methodElement.getAnnotation(Event.class);
        if (annotation != null)
            return annotation.value();
        else
            return methodElement.getAnnotation(DataRequest.class).value(); // if there is no @Event, there must be @Request
    }
    //endregion
}
