package com.philhacker.vice;

import java.util.Arrays;

/**
 * Created by mattdupree on 7/11/16.
 */
public class Invocation {
    private final Object target;
    private final Object returnValue;
    private final Object[] parameters;

    public Invocation(Object target, Object returnValue, Object... parameters) {
        this.target = target;
        this.returnValue = returnValue;
        this.parameters = parameters;
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
        return "reverse";
    }

    public Object[] getParameters() {
        return parameters;
    }
}
