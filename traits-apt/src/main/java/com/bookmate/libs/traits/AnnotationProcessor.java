package com.bookmate.libs.traits;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
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

    private final SourceHelper sourceHelper = new SourceHelper(); // cur SourceUtils, CodeGenerationUtils, make SourceHelper.map static
    /**
     * cur why do we need it?
     */
    private final Map<TypeElement, HelperClassBuilder> helpersMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA");
        sourceHelper.buildSourceClassesMap(processingEnv, roundEnv);

        for (Element e : roundEnv.getElementsAnnotatedWith(Event.class))
            addEventOrRequestListener(e);

        for (Element e : roundEnv.getElementsAnnotatedWith(DataRequest.class))
            addEventOrRequestListener(e);

        for (Map.Entry<TypeElement, HelperClassBuilder> helperEntry : helpersMap.entrySet()) // build helper classes
            CodeGenerationHelper.writeClassToFile(helperEntry.getValue().buildClass(), Utils.extractPackageName(helperEntry.getKey()), processingEnv);
        return true; // no further processing of this annotation type
    }

    protected void addEventOrRequestListener(Element e) {
        final ExecutableElement methodElement = (ExecutableElement) e; // CUR check
        final HelperClassBuilder helperBuilder = getHelperClass((TypeElement) methodElement.getEnclosingElement());

        final ClassName eventOrRequestClassName = sourceHelper.getEventOrRequestClassName(methodElement); // cur what if null
        final String listenerName = Utils.toLowerCaseFirstCharacter(eventOrRequestClassName.simpleName()) + "Listener" + helperBuilder.getListenersCount(eventOrRequestClassName.simpleName());
        final TypeSpec listenerClass = CodeGenerationHelper.createListenerClass(methodElement, eventOrRequestClassName);

        helperBuilder.constructorBuilder.addCode("\n"); // to visually separate different event listeners
        helperBuilder.constructorBuilder.addStatement("$N = $L", listenerName, listenerClass).build();
        helperBuilder.constructorBuilder.addStatement("$N.register($T.class, $N)", HelperClassBuilder.ACCESS_BUS, eventOrRequestClassName, listenerName).build();

        helperBuilder.classBuilder.addField(listenerClass.superclass, listenerName, Modifier.PRIVATE, Modifier.FINAL).build();
    }

    protected HelperClassBuilder getHelperClass(TypeElement classElement) {
        HelperClassBuilder helperClassBuilder = helpersMap.get(classElement);
        if (helperClassBuilder != null)
            return helperClassBuilder;

        helperClassBuilder = CodeGenerationHelper.createHelperClassBuilder(classElement);
        helpersMap.put(classElement, helperClassBuilder);
        return helperClassBuilder;
    }

}
