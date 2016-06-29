package com.philhacker.vice.regressiontestwriter;

import com.philhacker.vice.Triple;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by mattdupree on 6/22/16.
 */
public interface RegressionTestWriter {
    MethodSpec getMethodSpec(Class classToClamp, String objectVariableName, String format, Triple<Method, List<Object>, Object> firstMethodInvocation);

    TypeSpec getTypeSpec(Class classToClamp, MethodSpec characterizeReverse);

    void write(Class classToClamp, TypeSpec characterizationClass) throws IOException;
}
