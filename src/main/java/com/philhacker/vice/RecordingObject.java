package com.philhacker.vice;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * Created by mattdupree on 6/22/16.
 */
class RecordingObject {
    private final Object rawObject;
    private final List<Triple<Method, List<Object>, Object>> args;

    private RecordingObject(Object rawObject, List<Triple<Method, List<Object>, Object>> args) {
        this.rawObject = rawObject;
        this.args = args;
    }

    static RecordingObject make(Class classToClamp) throws InstantiationException, IllegalAccessException {
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

    public Object getRawObject() {
        return rawObject;
    }

    public List<Triple<Method, List<Object>, Object>> getArgs() {
        return args;
    }

    private static DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition getReceiverTypeDefinition(DynamicType.Builder subclass, Method method, List<Triple<Method, List<Object>, Object>> methodToInvocations) {
        return subclass
                .method(named(method.getName()).and(takesArguments(method.getParameterCount())))
                .intercept(MethodDelegation.to(new MethodDelegate(method, methodToInvocations)));
    }

    //Public so that bytebuddy can access
    @SuppressWarnings("WeakerAccess")
    public static class MethodDelegate {
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
