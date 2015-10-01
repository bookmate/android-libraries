/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   23.09.15
 */
package com.bookmate.libs.traits;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
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
    public static TypeSpec createListenerClass(EventOrRequestMethod eventOrRequestMethod) {
        String parameterName = eventOrRequestMethod.isRequest ? "request" : "event";

        return TypeSpec.anonymousClassBuilder("").superclass(eventOrRequestMethod.listenerClass)
                .addMethod(MethodSpec.methodBuilder("process")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(eventOrRequestMethod.eventOrRequestClassName, parameterName)
                        .addStatement(createListenerMethodCode(eventOrRequestMethod), HelperClassBuilder.TRAIT_FIELD_NAME, Utils.extractMethodName(eventOrRequestMethod.element), parameterName)
                        .returns(eventOrRequestMethod.returnTypeName)
                        .build())
                .build();
    }

    public static void initializeListenerInConstructor(MethodSpec.Builder constructorBuilder, EventOrRequestMethod eventOrRequestMethod, String listenerName, TypeSpec listenerClass) {
        constructorBuilder.addCode("\n"); // to visually separate different event listeners
        constructorBuilder.addStatement("$N = $L", listenerName, listenerClass).build();
        constructorBuilder.addStatement("$N.register($T.class, $N)", HelperClassBuilder.ACCESS_BUS, eventOrRequestMethod.eventOrRequestClassName, listenerName).build();
    }

    /**
     * @throws IllegalArgumentException if method parameters are incorrect (more than one parameter or unsuitable type)
     */
    protected static String createListenerMethodCode(EventOrRequestMethod eventOrRequestMethod) {
        String methodCode = eventOrRequestMethod.element.getParameters().size() > 0 ? "$N.$N($N)" : "$N.$N()";
        if (eventOrRequestMethod.returnTypeName != TypeName.VOID)
            methodCode = "return " + methodCode;
        return methodCode;
    }

    public static void writeClassToFile(TypeSpec helperClass, String packageName, ProcessingEnvironment processingEnv) {
        final String classFullName = packageName + "." + helperClass.name;
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating source file for " + classFullName);

        JavaFile javaFile = JavaFile.builder(packageName, helperClass).indent("    ").build();

        try {
            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(classFullName);
            final Writer out = jfo.openWriter();
            javaFile.writeTo(out);
            out.close();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "I/O error: couldn't write " + classFullName + " class to file. Cause: " + e);
        }
    }
}
