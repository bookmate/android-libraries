package com.bookmate.libs.traits;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
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
import javax.tools.Diagnostic;
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
        for (Element elem : roundEnv.getElementsAnnotatedWith(Event.class)) {
            final TypeElement classElement = (TypeElement) elem.getEnclosingElement();

            final String traitHelperClassName = classElement.getSimpleName() + "Helper_";
//            MethodSpec main = MethodSpec.methodBuilder("main")
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(void.class)
//                    .addParameter(String[].class, "args")
//                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
//                    .build();

            MethodSpec flux = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(TypeName.get(classElement.asType()), "trait", Modifier.FINAL)
                    .addStatement("this.$N = $N", "trait", "trait")
                    .build();

            TypeSpec traitHelper = TypeSpec.classBuilder(traitHelperClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(flux)
                    .build();

            final String packageName = ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
            JavaFile javaFile = JavaFile.builder(packageName, traitHelper)
                    .build();

            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, packageName);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName + "." + traitHelperClassName);
                final Writer out = jfo.openWriter();
                javaFile.writeTo(out);
                out.close();

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
