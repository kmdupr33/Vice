package com.philhacker.vice;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by mattdupree on 6/22/16.
 */
interface RegressionTestWriter {
    MethodSpec getMethodSpec(Class classToClamp, String objectVariableName, String format, Triple<Method, List<Object>, Object> firstMethodInvocation);

    TypeSpec getTypeSpec(Class classToClamp, MethodSpec characterizeReverse);

    void write(Path s, Class classToClamp, TypeSpec characterizationClass) throws IOException;
}
