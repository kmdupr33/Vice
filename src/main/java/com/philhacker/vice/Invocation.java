package com.philhacker.vice;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by mattdupree on 7/11/16.
 */
public class Invocation {
    private final Object target;
    private final Object returnValue;
    private final String methodName;
    private final Object[] parameters;

    public Invocation(Object target, String methodName, Object returnValue, Object... parameters) {
        this.target = target;
        this.methodName = methodName;
        this.returnValue = returnValue;
        this.parameters = parameters;
    }

    private Method findMethod(Object target, Object returnValue, Object[] parameters) {
        for (Method method : target.getClass().getMethods()) {
            final boolean returnTypesMatch = method.getReturnType() == returnValue.getClass();
            if (returnTypesMatch && parameterTypesMatch(method.getParameterTypes(), parameters)) {
                return method;
            }
        }
        throw new IllegalArgumentException("cannot find method on " + target + " with return value " + returnValue + " and params: " + parameters);
    }

    private boolean parameterTypesMatch(Class<?>[] parameterTypes, Object[] parameters) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].equals(parameters[i].getClass())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invocation that = (Invocation) o;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        if (returnValue != null ? !returnValue.equals(that.returnValue) : that.returnValue != null) return false;
        return Arrays.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        int result = returnValue != null ? returnValue.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    public Object getTarget() {
        return target;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public Class<?> getMethodReturnValueType() {
        return returnValue.getClass();
    }

    public Object getReturnValue() {
        return returnValue;
    }
}
