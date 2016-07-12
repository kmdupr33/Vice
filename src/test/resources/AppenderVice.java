package com.philhacker.vice;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppenderVice {
    @Test
    void clampAppend() {
        ViceFactoryTests.Appender appender = new ViceFactoryTests.Appender();
        String result = appender.append("oll", "eh");
        assertEquals("olleh", result);
    }
}
