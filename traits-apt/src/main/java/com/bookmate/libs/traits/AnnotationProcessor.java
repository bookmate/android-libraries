package com.bookmate.libs.traits;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.bookmate.libs.traits.Event")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private final SourceHelper sourceHelper = new SourceHelper();
    private final Map<TypeElement, BuildingClass> helpersMap = new HashMap<>();

    private static class BuildingClass {
        public static final String TRAIT_FIELD_NAME = "trait";

        final TypeSpec.Builder classBuilder;
        final MethodSpec.Builder constructorBuilder;
        /**
         * how many listeners to this event we already generated in this class
         */
        final Map<String, Integer> listenersCountMap = new HashMap<>();

        public BuildingClass(TypeSpec.Builder classBuilder, MethodSpec.Builder constructorBuilder) {
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
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA");
        sourceHelper.buildClassesMap(processingEnv, roundEnv);
        for (Element e : roundEnv.getElementsAnnotatedWith(Event.class)) {
            ExecutableElement method = (ExecutableElement) e; // CUR check
            BuildingClass helper = getHelperClass((TypeElement) method.getEnclosingElement());

            final TypeName eventOrRequestClass = getEventOrRequestClass(method); // cur what if null
            final String eventOrRequestClassName = ((ClassName) eventOrRequestClass).simpleName();
            final String listenerName = Utils.toLowerCaseFirstCharacter(eventOrRequestClassName) + "Listener" + helper.getListenersCount(eventOrRequestClassName);
            final ParameterizedTypeName listenerClass = ParameterizedTypeName.get(ClassName.get(Bus.EventListener.class), eventOrRequestClass);
            final TypeSpec listener = TypeSpec.anonymousClassBuilder("").addSuperinterface(listenerClass)
                    .addMethod(MethodSpec.methodBuilder("onEvent")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(eventOrRequestClass, "event")
                            .addStatement(method.getParameters().size() > 0 ? "$N.$N($N)" : "$N.$N()", BuildingClass.TRAIT_FIELD_NAME, Utils.methodName(method), "event")
                            .build())
                    .build();

            helper.constructorBuilder.addStatement("$N = $L", listenerName, listener).build();
            helper.classBuilder.addField(listenerClass, listenerName, Modifier.PRIVATE, Modifier.FINAL).build();
        }

        for (Map.Entry<TypeElement, BuildingClass> helperEntry : helpersMap.entrySet()) {
            final String packageName = ((PackageElement) helperEntry.getKey().getEnclosingElement()).getQualifiedName().toString();
            final BuildingClass helper = helperEntry.getValue();

            final TypeSpec helperClass = helper.classBuilder.addMethod(helper.constructorBuilder.build()).build();
            JavaFile javaFile = JavaFile.builder(packageName, helperClass).build();
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + helperClass.name);
                final Writer out = jfo.openWriter();
                javaFile.writeTo(out);
                out.close();
            } catch (IOException ignored) {
            }
        }
        return true; // no further processing of this annotation type
    }

    private BuildingClass getHelperClass(TypeElement classElement) {
        BuildingClass buildingClass = helpersMap.get(classElement);
        if (buildingClass != null)
            return buildingClass;

        final TypeName traitTypeName = TypeName.get(classElement.asType());

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(traitTypeName, BuildingClass.TRAIT_FIELD_NAME, Modifier.FINAL)
                .addStatement("this.$N = $N", BuildingClass.TRAIT_FIELD_NAME, BuildingClass.TRAIT_FIELD_NAME);

        TypeSpec.Builder helperBuilder = TypeSpec.classBuilder(classElement.getSimpleName() + "Helper_")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(traitTypeName, BuildingClass.TRAIT_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL);

        buildingClass = new BuildingClass(helperBuilder, constructorBuilder);
        helpersMap.put(classElement, buildingClass);
        return buildingClass;
    }

    /**
     * Tries to extract event or request class info from annotation parameter, method parameter or method name
     * @return a {@link TypeName} object corresponding to the event class
     */
    private TypeName getEventOrRequestClass(ExecutableElement element) {
        try {
            Class<?> eventOrRequestClass;
            final Event annotation = element.getAnnotation(Event.class);
            if (annotation != null)
                eventOrRequestClass = annotation.value();
            else
                eventOrRequestClass = element.getAnnotation(Request.class).value(); // if there is no @Event, there must be @Request

            if (eventOrRequestClass != Object.class) // Object is default value of annotation parameter, so we check, whether the parameter was explicitly set.
                return ClassName.get(eventOrRequestClass);
        } catch (MirroredTypeException mte) { // http://hannesdorfmann.com/annotation-processing/annotationprocessing101#datamodel
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            final String qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            if (!"java.lang.Object".equals(qualifiedSuperClassName))
                return ClassName.get(classTypeElement);
        }

        if (element.getParameters().size() > 0)
            return ClassName.get(element.getParameters().get(0).asType());

        String methodName = Utils.methodName(element); // isPublic
        methodName = methodName.startsWith("on") ? methodName.substring(2) : methodName;
        methodName = Utils.toUpperCaseFirstCharacter(methodName);
        final TypeElement eventOrRequestTypeElement = sourceHelper.getTypeElement(methodName);
        if (eventOrRequestTypeElement != null)
            return ClassName.get(eventOrRequestTypeElement);

        return null;
    }

}
