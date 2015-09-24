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
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.bookmate.libs.traits.Event")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    /**
     * we process annotations one by one, so we need to create helper builder once for each type and then store it for later access
     */
    private final Map<TypeElement, HelperClassBuilder> helperBuildersMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        SourceUtils.buildSourceClassesMap(processingEnv, roundEnv);

        try {
            for (Element e : roundEnv.getElementsAnnotatedWith(Event.class))
                addEventOrRequestListener((ExecutableElement) e); // only methods can be annotated with @Event, so no need to check the cast here

            for (Element e : roundEnv.getElementsAnnotatedWith(DataRequest.class))
                addEventOrRequestListener((ExecutableElement) e);

            for (Map.Entry<TypeElement, HelperClassBuilder> helperEntry : helperBuildersMap.entrySet()) // build helper classes
                CodeGenerationUtils.writeClassToFile(helperEntry.getValue().buildClass(), Utils.extractPackageName(helperEntry.getKey()), processingEnv);
        } catch (IllegalArgumentException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        return true; // no further processing of this annotation type
    }

    protected void addEventOrRequestListener(ExecutableElement methodElement) {
        final HelperClassBuilder helperBuilder = getHelperClassBuilder((TypeElement) methodElement.getEnclosingElement());

        final ClassName eventOrRequestClassName = SourceUtils.getEventOrRequestClassName(methodElement); // cur what if null
        final String listenerName = Utils.toLowerCaseFirstCharacter(eventOrRequestClassName.simpleName()) + "Listener" + helperBuilder.getListenersCount(eventOrRequestClassName.simpleName());
        final TypeSpec listenerClass = CodeGenerationUtils.createListenerClass(methodElement, eventOrRequestClassName);

        helperBuilder.constructorBuilder.addCode("\n"); // to visually separate different event listeners
        helperBuilder.constructorBuilder.addStatement("$N = $L", listenerName, listenerClass).build();
        helperBuilder.constructorBuilder.addStatement("$N.register($T.class, $N)", HelperClassBuilder.ACCESS_BUS, eventOrRequestClassName, listenerName).build();

        helperBuilder.classBuilder.addField(listenerClass.superclass, listenerName, Modifier.PRIVATE, Modifier.FINAL).build();
    }

    /**
     * Creates {@link HelperClassBuilder} or retrieves stored one from cache
     */
    protected HelperClassBuilder getHelperClassBuilder(TypeElement classElement) {
        HelperClassBuilder helperClassBuilder = helperBuildersMap.get(classElement);
        if (helperClassBuilder != null)
            return helperClassBuilder;

        helperClassBuilder = CodeGenerationUtils.createHelperClassBuilder(classElement);
        helperBuildersMap.put(classElement, helperClassBuilder);
        return helperClassBuilder;
    }

}
