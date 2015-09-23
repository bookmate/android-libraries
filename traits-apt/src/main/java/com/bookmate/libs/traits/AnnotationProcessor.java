package com.bookmate.libs.traits;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes("com.bookmate.libs.traits.Event")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private final SourceHelper sourceHelper = new SourceHelper();
    private final Map<TypeElement, HelperClassBuilder> helpersMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA");
        sourceHelper.buildSourceClassesMap(processingEnv, roundEnv);
        for (Element e : roundEnv.getElementsAnnotatedWith(Event.class)) {
            ExecutableElement methodElement = (ExecutableElement) e; // CUR check
            HelperClassBuilder helper = getHelperClass((TypeElement) methodElement.getEnclosingElement());

            final ClassName eventOrRequestClassName = sourceHelper.getEventOrRequestClassName(methodElement); // cur what if null
            final String listenerName = Utils.toLowerCaseFirstCharacter(eventOrRequestClassName.simpleName()) + "Listener" + helper.getListenersCount(eventOrRequestClassName.simpleName());
            final ParameterizedTypeName listenerClass = ParameterizedTypeName.get(ClassName.get(Bus.EventListener.class), eventOrRequestClassName);
            final TypeSpec listener = TypeSpec.anonymousClassBuilder("").addSuperinterface(listenerClass)
                    .addMethod(MethodSpec.methodBuilder("onEvent")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(eventOrRequestClassName, "event")
                            .addStatement(methodElement.getParameters().size() > 0 ? "$N.$N($N)" : "$N.$N()", HelperClassBuilder.TRAIT_FIELD_NAME, Utils.extractMethodName(methodElement), "event")
                            .build())
                    .build();

            helper.constructorBuilder.addStatement("$N = $L", listenerName, listener).build();
            helper.classBuilder.addField(listenerClass, listenerName, Modifier.PRIVATE, Modifier.FINAL).build();
        }

        buildHelperClasses();
        return true; // no further processing of this annotation type
    }

    private HelperClassBuilder getHelperClass(TypeElement classElement) {
        HelperClassBuilder helperClassBuilder = helpersMap.get(classElement);
        if (helperClassBuilder != null)
            return helperClassBuilder;

        helperClassBuilder = CodeGenerationHelper.createHelperClassBuilder(classElement);
        helpersMap.put(classElement, helperClassBuilder);
        return helperClassBuilder;
    }

    private void buildHelperClasses() {
        for (Map.Entry<TypeElement, HelperClassBuilder> helperEntry : helpersMap.entrySet())
            CodeGenerationHelper.writeClassToFile(helperEntry.getValue().buildClass(), Utils.extractPackageName(helperEntry.getKey()), processingEnv);
    }

}
