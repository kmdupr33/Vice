package com.philhacker.vice;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        final Class<?> targetClassName = invocation.getTarget().getClass();
        final String targetVariableName = targetClassName.getSimpleName().toLowerCase();
        final String methodNameSuffix = getMethodNameSuffix(invocation);

        StringBuilder actMethodInvocationStringBuilder = new StringBuilder("$T result = $L.$L(");
        for (Object param : invocation.getParameters()) {
            actMethodInvocationStringBuilder.append("$S, ");
        }
        actMethodInvocationStringBuilder.delete(actMethodInvocationStringBuilder.length() - 2, actMethodInvocationStringBuilder.length());
        actMethodInvocationStringBuilder.append(")");

        List<Object> statementFormatStringArgs = new ArrayList<>();
        statementFormatStringArgs.add(invocation.getMethodReturnValueType());
        statementFormatStringArgs.add(targetVariableName);
        statementFormatStringArgs.add(invocation.getMethodName());
        statementFormatStringArgs.addAll(Arrays.asList(invocation.getParameters()));

        final MethodSpec testMethod = MethodSpec.methodBuilder("clamp" + methodNameSuffix)
                .addAnnotation(Test.class)
                .addStatement("$T $L = new $T()", targetClassName, targetVariableName, targetClassName)
                .addStatement(actMethodInvocationStringBuilder.toString(), statementFormatStringArgs.toArray())
                .addStatement("assertEquals($S, result)", "olleh")
                .build();

        final TypeSpec testClass = TypeSpec.classBuilder(targetClassName.getSimpleName() + "Vice")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(testMethod)
                .build();

        JavaFile javaFile = JavaFile.builder(targetClassName.getPackage().getName(), testClass)
                .indent("    ")
                .addStaticImport(org.junit.Assert.class, "assertEquals")
                .build();

        final StringBuilder out = new StringBuilder();
        javaFile.writeTo(out);
        return out.toString();
    }

    private String getMethodNameSuffix(Invocation invocation) {
        return invocation.getMethodName().substring(0, 1).toUpperCase() + invocation.getMethodName().substring(1);
    }
}
