package com.philhacker.vice.regressiontestwriter;

import com.philhacker.vice.Triple;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import junit.framework.TestCase;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

/**
 * Regression test writer that uses the javapoet library.
 *
 * TODO Rewrite methods in langauge of our domain
 *
 * Created by mattdupree on 6/22/16.
 */
public class JavaPoetRegressionTestWriter implements RegressionTestWriter {
    public MethodSpec getMethodSpec(Class classToClamp, String objectVariableName, String format, Triple<Method, List<Object>, Object> firstMethodInvocation) {
        return MethodSpec.methodBuilder("characterizeReverse")
                                .addModifiers(Modifier.PUBLIC)
                                .returns(void.class)
                                .addAnnotation(Test.class)
                                .addStatement(format, classToClamp, classToClamp)
                                .addStatement("final $T result = $L.$L($S)", firstMethodInvocation.getV().getClass(),
                                              objectVariableName,
                                              firstMethodInvocation.getT().getName(),
                                              firstMethodInvocation.getU().get(0))
                                .addStatement("assertEquals($S, result)", firstMethodInvocation.getV())
                                .build();
    }

    public TypeSpec getTypeSpec(Class classToClamp, MethodSpec characterizeReverse) {
        final String className = classToClamp.getSimpleName() + "Characterization";
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(characterizeReverse)
                .build();
    }

    public void write(Path s, Class classToClamp, TypeSpec characterizationClass) throws IOException {
        JavaFile javaFile = JavaFile.builder(classToClamp.getPackage().getName(), characterizationClass)
                .addStaticImport(TestCase.class, "assertEquals")
                .build();
        javaFile.writeTo(s);
    }
}
