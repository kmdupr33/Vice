package com.philhacker.vice;

/**
 * Created by mattdupree on 6/22/16.
 */
class Triple<T, U, V> {
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
