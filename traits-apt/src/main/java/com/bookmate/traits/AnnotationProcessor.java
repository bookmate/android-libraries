package com.bookmate.traits;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.bookmate.traits.Event")
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "AAAA");
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AAAA");
//        for (Element elem : roundEnv.getElementsAnnotatedWith(Event.class)) {
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
        return true; // no further processing of this annotation type
    }

//    @Override
//    public Set<String> getSupportedAnnotationTypes() { // CUR
//        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "ABAA");
//        final Set<String> supportedAnnotationTypes = new HashSet<>(super.getSupportedAnnotationTypes());
//        supportedAnnotationTypes.add("com.bookmate.traits.Event");
//        return supportedAnnotationTypes;
//    }
}
