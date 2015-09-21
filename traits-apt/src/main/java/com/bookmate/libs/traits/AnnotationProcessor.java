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

        public BuildingClass(TypeSpec.Builder classBuilder, MethodSpec.Builder constructorBuilder) {
            this.classBuilder = classBuilder;
            this.constructorBuilder = constructorBuilder;
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA");
        sourceHelper.init(processingEnv, roundEnv);
        for (Element e : roundEnv.getElementsAnnotatedWith(Event.class)) {
            ExecutableElement element = (ExecutableElement) e; // CUR check
            BuildingClass helper = getHelperClass((TypeElement) element.getEnclosingElement());

            final TypeName eventOrRequestClassName = getEventOrRequestClassName(element);
            final String listenerName = ((ClassName) eventOrRequestClassName).simpleName() + "Listener"; // cur
            final ParameterizedTypeName listenerClass = ParameterizedTypeName.get(ClassName.get(Bus.EventListener.class), eventOrRequestClassName);
            final TypeSpec listener = TypeSpec.anonymousClassBuilder("").addSuperinterface(listenerClass)
                    .addMethod(MethodSpec.methodBuilder("onEvent")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(eventOrRequestClassName, "event")
                            .addStatement("$N.$N($N)", BuildingClass.TRAIT_FIELD_NAME, Utils.methodName(element), "event")
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
     * tries to extract from annotation parameter, method parameter, method name
     */
    private TypeName getEventOrRequestClassName(ExecutableElement element) {
        try {
            Class<?> eventOrRequestClass;
            final Event annotation = element.getAnnotation(Event.class);
            if (annotation != null)
                eventOrRequestClass = annotation.value();
            else
                eventOrRequestClass = element.getAnnotation(Request.class).value(); // if there is no @Event, there must be @Request

            if (eventOrRequestClass != Object.class)
                return ClassName.get(eventOrRequestClass);
        } catch (MirroredTypeException mte) { // http://hannesdorfmann.com/annotation-processing/annotationprocessing101/
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            final String qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
            if (!"java.lang.Object".equals(qualifiedSuperClassName))
                return ClassName.get(classTypeElement);
        }

        if (element.getParameters().size() > 0)
            return ClassName.get(element.getParameters().get(0).asType());

        String methodName = Utils.methodName(element); // isPublic
//            if (Character.isUpperCase(methodName.charAt(0))) { cur
//                methodNameEqualsClassNameError(element, methodName);
//                return null;
//            }
        methodName = methodName.startsWith("on") ? methodName.substring(2) : methodName;
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1); // making it start from an uppercase letter
        final TypeElement eventOrRequestTypeElement = sourceHelper.getTypeElement(methodName);
        if (eventOrRequestTypeElement != null)
            return ClassName.get(eventOrRequestTypeElement);

        return null;
    }

}
