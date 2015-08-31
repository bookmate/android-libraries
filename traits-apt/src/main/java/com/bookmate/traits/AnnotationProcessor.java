package com.bookmate.traits;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.bookmate.traits.Event")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
                .build();

//        if (annotations.size() > 0)
//            for (TypeElement annotation : annotations)
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA" + annotation.getEnclosingElement().getSimpleName() + " " + annotation.getSimpleName());
        for (Element elem : roundEnv.getElementsAnnotatedWith(Event.class)) {
            final Element classElement = elem.getEnclosingElement();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, elem.getSimpleName() + " BBB " + classElement.getSimpleName());
            try {
                JavaFileObject jfo = null;
                jfo = processingEnv.getFiler().createSourceFile(classElement.getSimpleName());
                javaFile.writeTo(jfo.openWriter());
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Event complexity = elem.getAnnotation(Event.class);
//            String message = "annotation found in " + elem.getSimpleName()
//                    + " with complexity " ;
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
//            if (elem.getKind() == ElementKind.CLASS) {
//                TypeElement classElement = (TypeElement) elem;
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
