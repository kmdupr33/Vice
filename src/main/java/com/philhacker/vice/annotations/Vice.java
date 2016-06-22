package com.philhacker.vice.annotations;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * Created by mattdupree on 6/21/16.
 */
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
                final ByteBuddy byteBuddy = new ByteBuddy();
                final DynamicType.Builder subclass = byteBuddy
                        .subclass(classToClamp);
                DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition clampedClassTypeDef = null;
                for (Method method : classToClamp.getDeclaredMethods()) {
                    if (clampedClassTypeDef == null) {
                        clampedClassTypeDef = getReceiverTypeDefinition(subclass, method);
                        continue;
                    }
                    clampedClassTypeDef = getReceiverTypeDefinition(clampedClassTypeDef, method);
                }
                final Object clampedObject = clampedClassTypeDef
                        .make()
                        .load(classToClamp.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                        .getLoaded()
                        .newInstance();
                for (Method method : methodsToExecute) {
                    method.invoke(viceMaker, clampedObject);
                    final String objectVariableName = classToClamp.getSimpleName().toLowerCase();
                    final String format = String.format("$T %s = new $T()", objectVariableName);
                    MethodSpec characterizeReverse = MethodSpec.methodBuilder("characterizeReverse")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(void.class)
                            .addAnnotation(Test.class)
                            .addStatement(format, classToClamp, classToClamp)
                            .addStatement("$L", objectVariableName)
                            .build();
                    final String className = classToClamp.getSimpleName() + "Characterization";
                    TypeSpec characterizationClass = TypeSpec.classBuilder(className)
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(characterizeReverse)
                            .build();

                    JavaFile javaFile = JavaFile.builder(classToClamp.getPackage().getName(), characterizationClass)
                            .build();
                    javaFile.writeTo(s);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition getReceiverTypeDefinition(DynamicType.Builder subclass, Method method) {
        return subclass
                .method(named(method.getName()).and(takesArguments(method.getParameterCount())))
                .intercept(MethodDelegation.to(new MethodDelegate()));
    }

    public static class MethodDelegate {

        private final List<Object> args = new ArrayList<>();

        @SuppressWarnings("unused")
        @RuntimeType
        public Object reverse(@SuperCall Callable<Object> superz, @AllArguments Object[] args) throws Exception {
            captureArgs(args);
            return superz.call();
        }

        private void captureArgs(Object[] args) {
            System.out.println("captured args: " + Arrays.toString(args));
            this.args.addAll(Arrays.asList(args));
        }

        @SuppressWarnings("unused")
        @RuntimeType
        public void interceptVoidMethod(@SuperCall Callable<Void> zuper, @AllArguments Object[] args) throws Exception {
            captureArgs(args);
            zuper.call();
        }
    }
}
