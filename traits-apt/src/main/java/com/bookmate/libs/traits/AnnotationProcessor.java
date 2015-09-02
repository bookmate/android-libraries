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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.bookmate.libs.traits.Event")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        if (annotations.size() > 0)
//            for (TypeElement annotation : annotations)
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA" + annotation.getEnclosingElement().getSimpleName() + " " + annotation.getSimpleName());
        for (Element element : roundEnv.getElementsAnnotatedWith(Event.class)) {
            final TypeElement classElement = (TypeElement) element.getEnclosingElement();
            final String methodName = element.getSimpleName().toString(); // isPublic

//            if (Character.isUpperCase(methodName.charAt(0))) { cur
//                methodNameEqualsClassNameError(element, methodName);
//                return null;
//            }
//            TypeMirror classType = annotationHelper.extractAnnotationClassParameter(element, getTarget());
//
//            if (classType == null && firstParamType == ParamType.EVENT_OR_REQUEST) // trying to extract the event/request class from parameters
//                classType = element.getParameters().get(0).asType();
//            if (classType == null && secondParamType == ParamType.EVENT_OR_REQUEST) // trying to extract the event/request class from parameters
//                classType = element.getParameters().get(1).asType();
//
//            String className, extractedName = null;
//            if (classType != null) {
//                className = classType.toString();
//            } else {
//                extractedName = methodName.startsWith("on") ? methodName.substring(2) : methodName;
//                extractedName = extractedName.substring(0, 1).toUpperCase() + extractedName.substring(1); // making it start from an uppercase letter
//                className = SourceHelper.getClassesFullNameMap(processingEnv).get(extractedName); // trying to guess the package
//                if (className == null) {
//                    classNotFoundError(element, methodName, extractedName);
//                    return null;
//                }
//            }
            final String traitHelperClassName = classElement.getSimpleName() + "Helper_";
//            MethodSpec main = MethodSpec.methodBuilder("main")
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(void.class)
//                    .addParameter(String[].class, "args")
//                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
//                    .build();

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
                    .addField(ParameterizedTypeName.get(ClassName.get(Bus.EventListener.class), ClassName.get("com.bookmate.libs.demo.traits.readercode", "PageTurn")), traitFieldName, Modifier.PRIVATE, Modifier.FINAL)
                    .addField(ClassName.get("", "PageTurn"), traitFieldName, Modifier.PRIVATE, Modifier.FINAL)
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

            } catch (IOException e) {
                e.printStackTrace();
            }
//            Event complexity = element.getAnnotation(Event.class);
//            String message = "annotation found in " + element.getSimpleName()
//                    + " with complexity " ;
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
//            if (element.getKind() == ElementKind.CLASS) {
//                TypeElement classElement = (TypeElement) element;
//                PackageElement packageElement =
//                        (PackageElement) classElement.getEnclosingElement();
//
//                try {
////                    processingEnv.getFiler().getResource()
//                    final String srcName = String.valueOf(classElement.getSimpleName());
//                    if (srcName.endsWith("_"))
//                        continue;
//                    final String suffix = "_";
//                    final String name = srcName + suffix;
//                    JavaFileObject jfo = processingEnv.getFiler().createSourceFile(classElement.getQualifiedName() + suffix);
//                    BufferedWriter bw = new BufferedWriter(jfo.openWriter());
//                    bw.append("package ");
//                    bw.append(packageElement.getQualifiedName());
//                    bw.append(";");
//                    bw.newLine();
//                    bw.newLine();
//                    bw.append("class ").append(name).append(" extends ").append(srcName).append(" {}");
//                    bw.close();
//                } catch (IOException e) {
//                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, e.toString());
//                    e.printStackTrace();
//                }
//
//            }
//        }
        }
        return true; // no further processing of this annotation type
    }

}
