package com.philhacker.vice;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;

/**
 * Created by mattdupree on 7/11/16.
 */
public class ViceFactory {

    /**
     *
      * @param viceSpec
     * @return a string of the java source for the tests that act as a vice for refactoring
     */
    public String make(ViceSpec viceSpec) throws IOException {

        final Invocation invocation = viceSpec.getInvocations().get(0);
        final Class<?> aClass = invocation.getTarget().getClass();
        final String targetVariableName = aClass.getSimpleName().toLowerCase();
        final MethodSpec testMethod = MethodSpec.methodBuilder("clampReverse")
                .addAnnotation(Test.class)
                .addStatement("$T $L = new $T()", aClass, targetVariableName, aClass)
                .addStatement("$T result = $L.$L($S)", String.class, targetVariableName, invocation.getMethodName(), invocation.getParameters()[0])
                .addStatement("assertEquals($S, result)", "olleh")
                .build();

        final TypeSpec testClass = TypeSpec.classBuilder("ReverserVice")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(testMethod)
                .build();

        JavaFile javaFile = JavaFile.builder("com.philhacker.vice", testClass)
                .indent("    ")
                .addStaticImport(org.junit.Assert.class, "assertEquals")
                .build();

        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);
        return out.toString();
    }
}
