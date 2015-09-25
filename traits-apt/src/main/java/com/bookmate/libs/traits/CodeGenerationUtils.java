/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.09.15
 */
package com.bookmate.libs.traits;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

public class CodeGenerationUtils {

    /**
     * Initializes constructor builder etc
     */
    public static HelperClassBuilder createHelperClassBuilder(TypeElement classElement) {
        final TypeName traitTypeName = TypeName.get(classElement.asType());

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(traitTypeName, HelperClassBuilder.TRAIT_FIELD_NAME, Modifier.FINAL)
                .addStatement("this.$N = $N", HelperClassBuilder.TRAIT_FIELD_NAME, HelperClassBuilder.TRAIT_FIELD_NAME);

        TypeSpec.Builder helperBuilder = TypeSpec.classBuilder(classElement.getSimpleName() + "Helper_")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(traitTypeName, HelperClassBuilder.TRAIT_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL);

        return new HelperClassBuilder(helperBuilder, constructorBuilder);
    }

    /**
     * Creates listener anonymous class. Event and DataRequest listeners code generation has very much in common, so I use this not very elegant code here
     */
    public static TypeSpec createListenerClass(ExecutableElement methodElement, ClassName eventOrRequestClassName) {
        final TypeName methodReturnTypeName = extractMethodReturnTypeName(methodElement);

        final boolean isRequest = Utils.isRequest(methodElement);
        String parameterName = isRequest ? "request" : "event";

        return TypeSpec.anonymousClassBuilder("").superclass(getListenerBaseClass(eventOrRequestClassName, methodReturnTypeName, isRequest))
                .addMethod(MethodSpec.methodBuilder("process")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(eventOrRequestClassName, parameterName)
                        .addStatement(createListenerMethodCode(methodElement, methodReturnTypeName), HelperClassBuilder.TRAIT_FIELD_NAME, Utils.extractMethodName(methodElement), parameterName)
                        .returns(methodReturnTypeName)
                        .build())
                .build();
    }

    protected static TypeName extractMethodReturnTypeName(ExecutableElement methodElement) {
        final TypeName typeName = TypeName.get(methodElement.getReturnType());
        return typeName == TypeName.VOID ? typeName : typeName.box(); // needed to make int -> Integer, but leave void as is
    }

    protected static ParameterizedTypeName getListenerBaseClass(ClassName eventOrRequestClassName, TypeName methodReturnTypeName, boolean isRequest) {
        if (!isRequest)
            return ParameterizedTypeName.get(ClassName.get(Bus.EventListener.class), eventOrRequestClassName);
        return ParameterizedTypeName.get(ClassName.get(Bus.DataRequestListener.class), methodReturnTypeName, eventOrRequestClassName); // cur check situation like Bus.DataRequestListener<Document, GetTappedMarkerColor>
    }

    protected static String createListenerMethodCode(ExecutableElement methodElement, TypeName methodReturnTypeName) {
        String methodCode = methodElement.getParameters().size() > 0 ? "$N.$N($N)" : "$N.$N()";
        if (methodReturnTypeName != TypeName.VOID)
            methodCode = "return " + methodCode;
        return methodCode;
    }

    public static void writeClassToFile(TypeSpec helperClass, String packageName, ProcessingEnvironment processingEnv) {
        JavaFile javaFile = JavaFile.builder(packageName, helperClass).indent("    ").build();

        try {
            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + helperClass.name);
            final Writer out = jfo.openWriter();
            javaFile.writeTo(out);
            out.close();
        } catch (IOException ignored) { // cur handle io error
        }
    }
}
