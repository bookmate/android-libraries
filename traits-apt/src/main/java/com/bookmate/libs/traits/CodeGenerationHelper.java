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

public class CodeGenerationHelper {

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

    public static TypeSpec createListenerOrProcessor(ExecutableElement methodElement, ParameterizedTypeName listenerOrProcessorClass, String methodName, String parameterName) {
        final TypeName methodReturnTypeName = TypeName.get(methodElement.getReturnType());

        String methodCode = methodElement.getParameters().size() > 0 ? "$N.$N($N)" : "$N.$N()";
        if (methodReturnTypeName != TypeName.VOID)
            methodCode = "return " + methodCode;

        return TypeSpec.anonymousClassBuilder("").addSuperinterface(listenerOrProcessorClass)
                    .addMethod(MethodSpec.methodBuilder(methodName)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(listenerOrProcessorClass.typeArguments.get(0), parameterName)
                            .addStatement(methodCode, HelperClassBuilder.TRAIT_FIELD_NAME, Utils.extractMethodName(methodElement), parameterName)
                            .returns(methodReturnTypeName)
                            .build())
                    .build();
    }

    public static void writeClassToFile(TypeSpec helperClass, String packageName, ProcessingEnvironment processingEnv) {
        JavaFile javaFile = JavaFile.builder(packageName, helperClass).build();

        try {
            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + helperClass.name);
            final Writer out = jfo.openWriter();
            javaFile.writeTo(out);
            out.close();
        } catch (IOException ignored) { // cur handle io error
        }
    }
}
