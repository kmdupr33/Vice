package com.philhacker.vice;

/**
 * Created by mattdupree on 6/20/16.
 */
public class Reverser {
    public String reverse(String string) {
        return new StringBuilder(string).reverse().toString();
    }

}
