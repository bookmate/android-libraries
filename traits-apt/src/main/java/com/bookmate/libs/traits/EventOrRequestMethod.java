/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   25.09.15
 */
package com.bookmate.libs.traits;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;

public class EventOrRequestMethod {
    public final ExecutableElement element;
    public final TypeName returnTypeName;
    public final ClassName eventOrRequestClassName;
    public final boolean isRequest;
    public final ParameterizedTypeName listenerClass;

    public EventOrRequestMethod(ExecutableElement methodElement) {
        element = methodElement;
        eventOrRequestClassName = SourceUtils.getEventOrRequestClassName(methodElement);
        returnTypeName = extractMethodReturnTypeName(methodElement);
        isRequest = Utils.isRequest(element);
        listenerClass = getListenerClass();

        assertMethodIsCorrect();
    }

    /**
     * @throws IllegalArgumentException if method is private or method parameters are incorrect (more than one parameter or unsuitable type)
     */
    protected void assertMethodIsCorrect() {
        if (element.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE))
            throw new IllegalArgumentException("Methods annotated with " + Utils.getAnnotationNameString(element) + " mustn't be private");

        if (element.getParameters().size() > 1)
            throw new IllegalArgumentException("Methods annotated with " + Utils.getAnnotationNameString(element) + " must have 0 or 1 parameters");

        // parameter type if present must equal event or request type
        if (element.getParameters().size() > 0) {
            final TypeName parameterTypeName = ClassName.get(element.getParameters().get(0).asType());
            if (!eventOrRequestClassName.equals(parameterTypeName))
                throw new IllegalArgumentException("Method parameter type should be " + eventOrRequestClassName + " Now it's " + parameterTypeName);
        }
    }

    protected ParameterizedTypeName getListenerClass() {
        if (!isRequest)
            return ParameterizedTypeName.get(ClassName.get(Bus.EventListener.class), eventOrRequestClassName);
        return ParameterizedTypeName.get(ClassName.get(Bus.DataRequestListener.class), returnTypeName, eventOrRequestClassName); // cur check situation like Bus.DataRequestListener<Document, GetTappedMarkerColor>
    }

    protected static TypeName extractMethodReturnTypeName(ExecutableElement methodElement) {
        final TypeName typeName = TypeName.get(methodElement.getReturnType());
        return typeName == TypeName.VOID ? typeName : typeName.box(); // needed to make int -> Integer, but leave void as is
    }

}
