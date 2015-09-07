/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.03.15
 */
package com.bookmate.libs.traits;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Needed to guess class package by name. The problem is that when we write @Event void onMyEvent(), the event class name is MyEvent, but we can't know the package.
 * This class tries to guess the sources dir by the manifest file name and then parses that directory looking for java classes and guesses the package name by path. It works pretty fast (indexing ~500 classes took less than a second).
 */
class SourceHelper {
    //    private static Map<String, String> classNameToFullName = new HashMap<>();
    private static Map<String, TypeElement> classNameToFullName = new HashMap<>();
//    private static final Map<String, JClass> classes = new HashMap<>();

    /**
     * this helper accumulates classes from each processing round
     */
    public void init(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        final long startTime = System.currentTimeMillis();
        for (Element element : roundEnv.getRootElements()) {
//            classNameToFullName.put(element.getSimpleName().toString(), ((TypeElement) element).getQualifiedName().toString());
            classNameToFullName.put(element.getSimpleName().toString(), (TypeElement) element);
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, element.getSimpleName() + " BBBB " + ((TypeElement) element).getQualifiedName());
        }
//        final Iterator<File> fileIterator = FileUtils.iterateFiles(srcDir, new String[]{"java"}, true);
//        while (fileIterator.hasNext()) {
//            final File file = fileIterator.next();
//            final String absolutePath = file.getAbsolutePath();
//            classNameToFullName.put(FilenameUtils.removeExtension(file.getName()), absolutePath.substring(srcPath.length() + 1, absolutePath.length() - 5).replace(File.separatorChar, '.')); // result: com.bookmate.events.IsPublic
//        }
//        for (CharSequence charSequence : classNameToFullName.keySet()) {
//            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, charSequence + " " + charSequence.equals("PageTurn") + " " + "PageTurn".equals(charSequence.toString()));
//        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Traits source helper processed " + classNameToFullName.size() + " classes in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    public TypeElement getTypeElement(String simpleName) {
        return classNameToFullName.get(simpleName);
    }
//    public static Map<String, String> getClassesFullNameMap(ProcessingEnvironment processingEnv) {
//        if (classNameToFullName == null)
//            init(processingEnv);
//        return classNameToFullName;
//    }

//    public static JClass getClass(String className) {
//        return classes.get(className);
//    }
//
//    public static void addClass(String className, JClass jClass) {
//        classes.put(className, jClass);
//    }
}
