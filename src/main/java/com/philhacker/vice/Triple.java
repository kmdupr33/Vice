package com.philhacker.vice;

/**
 * Simple utility class for wrapping three values
 *
 * Created by mattdupree on 6/22/16.
 */
public class Triple<T, U, V> {
    private final T t;
    private final U u;
    private final V v;

    public Triple(T t, U u, V v) {
        this.t = t;
        this.u = u;
        this.v = v;
    }

    public V getV() {
        return v;
    }

    public U getU() {
        return u;
    }

    public T getT() {
        return t;
    }
}
