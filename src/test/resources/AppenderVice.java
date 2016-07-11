package com.philhacker.vice;

import static org.junit.Assert.assertEquals;

import java.lang.String;
import org.junit.Test;

public class AppenderVice {
    @Test
    void clampAppend() {
        ViceFactoryTests.Appender appender = new ViceFactoryTests.Appender();
        String result = appender.append("oll", "eh");
        assertEquals("olleh", result);
    }
}
