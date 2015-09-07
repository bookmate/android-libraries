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
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        sourceHelper.init(processingEnv, roundEnv);
        for (Element e : roundEnv.getElementsAnnotatedWith(Event.class)) {
            ExecutableElement element = (ExecutableElement) e;
            final TypeElement classElement = (TypeElement) element.getEnclosingElement();
            final String traitHelperClassName = classElement.getSimpleName() + "Helper_";

            final String traitFieldName = "trait";
            final TypeName traitTypeName = TypeName.get(classElement.asType());
            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(traitTypeName, traitFieldName, Modifier.FINAL)
                    .addStatement("this.$N = $N", traitFieldName, traitFieldName)
                    .build();

            TypeSpec traitHelper = TypeSpec.classBuilder(traitHelperClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addField(traitTypeName, traitFieldName, Modifier.PRIVATE, Modifier.FINAL)
                    .addField(ParameterizedTypeName.get(ClassName.get(Bus.EventListener.class), getEventOrRequestClassName(element)), "pageTurnListener", Modifier.PRIVATE, Modifier.FINAL)
                    .addMethod(constructor)
                    .build();

            final String packageName = ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
            JavaFile javaFile = JavaFile.builder(packageName, traitHelper)
                    .build();

//            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, packageName);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + traitHelperClassName);
                final Writer out = jfo.openWriter();
                javaFile.writeTo(out);
                out.close();

            } catch (IOException ignored) {
            }
        }
        return true; // no further processing of this annotation type
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

        String methodName = element.getSimpleName().toString(); // isPublic
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
