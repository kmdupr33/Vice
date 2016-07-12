package com.philhacker.vice;

import com.google.gson.Gson;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
        System.out.println(new Gson().toJson(viceSpec.getInvocations()));
        final Class<?> targetClass = invocation.getTarget().getClass();
        final String targetVariableName = targetClass.getSimpleName().toLowerCase();
        final String methodNameSuffix = getMethodNameSuffix(invocation);

        StringBuilder actMethodInvocationStringBuilder = new StringBuilder("$T result = $L.$L(");
        // TODO find cleaner way
        final Object[] parameters = invocation.getParameters();


        List<Object> statementFormatStringArgs = new ArrayList<>();
        statementFormatStringArgs.add(invocation.getMethodReturnValueType());
        statementFormatStringArgs.add(targetVariableName);
        statementFormatStringArgs.add(invocation.getMethodName());

        for (int i = 0; i < parameters.length; i ++) {
            actMethodInvocationStringBuilder.append("$L, ");
            statementFormatStringArgs.add("invocation.getParams().get("+i+")");
        }

        if (parameters.length > 0) {
            actMethodInvocationStringBuilder.delete(actMethodInvocationStringBuilder.length() - 2, actMethodInvocationStringBuilder.length());
        }
        actMethodInvocationStringBuilder.append(")");

        final MethodSpec.Builder testMethodBuilder = MethodSpec.methodBuilder("clamp" + methodNameSuffix)
                .addAnnotation(Test.class)
                .addException(URISyntaxException.class)
                .addException(IOException.class)
                //final InputStream resource = this.getClass().getClassLoader()
                // .getResource("PersonViceMaker.json").openStream();
                .addStatement("$T resource = this.getClass().getClassLoader().getResource($S).openStream()", InputStream.class, targetClass.getSimpleName() + ".json");

        // ViceFactoryTests.PersonMaker.Person personMakerMakeResult = new Gson().fromJson(new InputStreamReader(resource),
        //        ViceFactoryTests.PersonMaker.Person.class);
        if (!invocation.getMethodReturnValueType().equals(Void.class)) {
            testMethodBuilder.addStatement("$T invocation = new $T().fromJson(new $T(resource), $T.class)",
                                           Invocation.class, Invocation.class, InputStreamReader.class, Invocation.class);
        }
        final MethodSpec methodSpec = testMethodBuilder.addStatement("$T $L = new $T()", targetClass, targetVariableName, targetClass)
                .addStatement(actMethodInvocationStringBuilder.toString(), statementFormatStringArgs.toArray())
                .addStatement("assertEquals(invocation.getReturnValue(), result)").build();

        final TypeSpec testClass = TypeSpec.classBuilder(targetClass.getSimpleName() + "Vice")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec)
                .build();

        JavaFile javaFile = JavaFile.builder(targetClass.getPackage().getName(), testClass)
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
