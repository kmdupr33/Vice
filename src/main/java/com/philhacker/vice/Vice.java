package com.philhacker.vice;

import com.philhacker.vice.annotations.Clamp;
import com.philhacker.vice.annotations.ViceFor;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import junit.framework.TestCase;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates regression tests.
 *
 * Created by mattdupree on 6/21/16.
 */
@SuppressWarnings("WeakerAccess")
public class Vice {
    public void make(Path s, Class<?>... viceMakerClasses) {
        ByteBuddyAgent.install();
        for (Class<?> viceMakerClass : viceMakerClasses) {
            final ViceFor annotation = viceMakerClass.getAnnotation(ViceFor.class);
            final Class classToClamp = annotation.value();
            final List<Method> methodsToExecute = Stream.of(viceMakerClass.getMethods())
                    .filter(method -> method.getAnnotation(Clamp.class) != null)
                    .collect(Collectors.toList());
            try {
                final Object viceMaker = viceMakerClass.newInstance();
                final RecordingObject recordingObject = RecordingObject.make(classToClamp);
                for (Method method : methodsToExecute) {
                    method.invoke(viceMaker, recordingObject.getRawObject());
                    final String objectVariableName = classToClamp.getSimpleName().toLowerCase();
                    final String format = String.format("$T %s = new $T()", objectVariableName);
                    //noinspection SuspiciousMethodCalls
                    final Triple<Method, List<Object>, Object> firstMethodInvocation = recordingObject.getArgs().get(0);
                    MethodSpec characterizeReverse = MethodSpec.methodBuilder("characterizeReverse")
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
                    final String className = classToClamp.getSimpleName() + "Characterization";
                    TypeSpec characterizationClass = TypeSpec.classBuilder(className)
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(characterizeReverse)
                            .build();

                    JavaFile javaFile = JavaFile.builder(classToClamp.getPackage().getName(), characterizationClass)
                            .addStaticImport(TestCase.class, "assertEquals")
                            .build();
                    javaFile.writeTo(s);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }
        }
    }


}
