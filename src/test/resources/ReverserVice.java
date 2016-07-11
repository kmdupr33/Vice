package com.philhacker.vice;

import static org.junit.Assert.assertEquals;

import java.lang.String;
import org.junit.Test;

public class ReverserVice {
    @Test
    void clampReverse() {
        ViceFactoryTests.Reverser reverser = new ViceFactoryTests.Reverser();
        String result = reverser.reverse("hello");
        assertEquals("olleh", result);
    }
}
