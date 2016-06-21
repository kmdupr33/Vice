package com.philhacker.vice;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ReverserCharacterization {
    @Test
    public void characterizeReverse() {
        Reverser reverser = new Reverser();
        final String result = reverser.reverse("hello");
        assertEquals("olleh", result);
    }
}
