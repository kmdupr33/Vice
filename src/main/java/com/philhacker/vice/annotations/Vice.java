package com.philhacker.vice.annotations;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import junit.framework.TestCase;
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
 * Generates regression tests.
 *
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
                final RecordingObject recordingObject = makeRecordingObject(classToClamp);
                for (Method method : methodsToExecute) {
                    method.invoke(viceMaker, recordingObject.rawObject);
                    final String objectVariableName = classToClamp.getSimpleName().toLowerCase();
                    final String format = String.format("$T %s = new $T()", objectVariableName);
                    //noinspection SuspiciousMethodCalls
                    final Triple<Method, List<Object>, Object> firstMethodInvocation = recordingObject.args.get(0);
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

    private static class Triple<T, U, V> {
        private final T t;
        private final U u;
        private final V v;

        Triple(T t, U u, V v) {
            this.t = t;
            this.u = u;
            this.v = v;
        }

        V getV() {
            return v;
        }

        U getU() {
            return u;
        }

        T getT() {
            return t;
        }
    }


    private static class RecordingObject {
        private final Object rawObject;
        private final List<Triple<Method, List<Object>, Object>> args;

        RecordingObject(Object rawObject, List<Triple<Method, List<Object>, Object>> args) {
            this.rawObject = rawObject;
            this.args = args;
        }
    }

    private RecordingObject makeRecordingObject(Class classToClamp) throws InstantiationException, IllegalAccessException {
        final ByteBuddy byteBuddy = new ByteBuddy();
        final DynamicType.Builder subclass = byteBuddy
                .subclass(classToClamp);
        DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition clampedClassTypeDef = null;
        final List<Triple<Method, List<Object>, Object>> invocations = new ArrayList<>();
        for (Method method : classToClamp.getDeclaredMethods()) {
            if (clampedClassTypeDef == null) {
                clampedClassTypeDef = getReceiverTypeDefinition(subclass, method, invocations);
                continue;
            }
            clampedClassTypeDef = getReceiverTypeDefinition(clampedClassTypeDef, method, invocations);
        }
        final Object rawObject = clampedClassTypeDef
                .make()
                .load(classToClamp.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent())
                .getLoaded()
                .newInstance();
        return new RecordingObject(rawObject, invocations);
    }

    private DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition getReceiverTypeDefinition(DynamicType.Builder subclass, Method method, List<Triple<Method, List<Object>, Object>> methodToInvocations) {
        return subclass
                .method(named(method.getName()).and(takesArguments(method.getParameterCount())))
                .intercept(MethodDelegation.to(new MethodDelegate(method, methodToInvocations)));
    }

    private static class MethodDelegate {
        private final Method method;
        private final List<Triple<Method, List<Object>, Object>> invocations;

        MethodDelegate(Method method, List<Triple<Method, List<Object>, Object>> invocations) {
            this.method = method;
            this.invocations = invocations;
        }

        @SuppressWarnings("unused")
        @RuntimeType
        public Object reverse(@SuperCall Callable<Object> superz, @AllArguments Object[] args) throws Exception {
            final Object returnValue = superz.call();
            captureArgs(args, returnValue);
            return returnValue;
        }

        private void captureArgs(Object[] args, Object returnValue) {
            System.out.println("captured args: " + Arrays.toString(args));
            this.invocations.add(new Triple<>(method, Arrays.asList(args), returnValue));
        }

        @SuppressWarnings("unused")
        @RuntimeType
        public void interceptVoidMethod(@SuperCall Callable<Void> zuper, @AllArguments Object[] args) throws Exception {
            captureArgs(args, null);
            zuper.call();
        }
    }
}
