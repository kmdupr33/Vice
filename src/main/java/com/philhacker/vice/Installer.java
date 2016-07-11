package com.philhacker.vice;

/**
 * Created by mattdupree on 7/11/16.
 */
public class Installer {
    private Invocation invocation;

    public void onMethod(Object returnValue, Object... params) {
        invocation = new Invocation(returnValue, params);
    }

    public void onObject(Object object) {

    }

    public Invocation getInvocation() {
        return invocation;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
}
