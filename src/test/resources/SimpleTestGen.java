package com.philhacker.vice;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by mattdupree on 6/21/16.
 */
public class ReverserCharacterization {
    @Test
    public void characterizeReverse() {
        Reverser reverser = new Reverser();
        final String result = reverser.reverse("hello");
        assertEquals("olleh", result);
    }
}
