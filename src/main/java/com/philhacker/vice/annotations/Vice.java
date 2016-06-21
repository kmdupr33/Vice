package com.philhacker.vice.annotations;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
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
 * Created by mattdupree on 6/21/16.
 */
public class Vice {
    public void make(Path s, Class<?>... viceMakerClasses) {
        for (Class<?> viceMakerClass : viceMakerClasses) {
            final ViceFor annotation = viceMakerClass.getAnnotation(ViceFor.class);
            final Class classToInstantiate = annotation.value();
            final List<Method> methodsToExecute = Stream.of(viceMakerClass.getMethods())
                    .filter(method -> method.getAnnotation(Clamp.class) != null)
                    .collect(Collectors.toList());
            try {
                final Object viceMaker = viceMakerClass.newInstance();
                final Object clampedObject = classToInstantiate.newInstance();
                for (Method method : methodsToExecute) {
                    final Object returnValue = method.invoke(viceMaker, clampedObject);
                    final String format = String.format("$T %s = new $T()", classToInstantiate.getSimpleName().toLowerCase());
                    MethodSpec characterizeReverse = MethodSpec.methodBuilder("characterizeReverse")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(void.class)
                            .addAnnotation(Test.class)
                            .addStatement(format, classToInstantiate, classToInstantiate)
                            .build();
                    final String className = classToInstantiate.getSimpleName() + "Characterization";
                    TypeSpec characterizationClass = TypeSpec.classBuilder(className)
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(characterizeReverse)
                            .build();

                    JavaFile javaFile = JavaFile.builder(classToInstantiate.getPackage().getName(), characterizationClass)
                            .build();
                    javaFile.writeTo(s);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
