/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   18.09.15
 */
package com.bookmate.libs.traits;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class Utils {
    public static String extractMethodName(ExecutableElement element) {
        return element.getSimpleName().toString();
    }

    public static String extractPackageName(TypeElement typeElement) {
        return ((PackageElement) typeElement.getEnclosingElement()).getQualifiedName().toString();
    }

    public static String toUpperCaseFirstCharacter(String methodName) {
        return methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
    }

    public static String toLowerCaseFirstCharacter(String methodName) {
        return methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
    }

    public static String extractMethodSignature(ExecutableElement methodElement) {
        return ((TypeElement) methodElement.getEnclosingElement()).getQualifiedName() + "." + methodElement.getSimpleName() + "()";
    }
}
