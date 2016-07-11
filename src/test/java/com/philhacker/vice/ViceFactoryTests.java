package com.philhacker.vice;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by mattdupree on 7/11/16.
 */
public class ViceFactoryTests {

    private ViceFactory viceFactory;

    @Before
    public void makeFactory() {
        viceFactory = new ViceFactory();
    }

    @Test
    public void shouldMakeForSimpleParameter() throws URISyntaxException, IOException {

        viceFactory = new ViceFactory();
        final ViceSpec viceSpec = new ViceSpec();

        class Reverser {
            String reverse(String toReverse) {
                final String result = new StringBuilder(toReverse).reverse().toString();
                viceSpec.clampMethod(this, "reverse", result, toReverse);
                return result;
            }
        }

        new Reverser().reverse("hello");

        String vice = viceFactory.make(viceSpec);

        final String expected = getExpectedVice("ReverserVice.java");
        assertEquals(expected, vice);
    }

    private String getExpectedVice(String fileName) throws URISyntaxException, IOException {
        final URI testFile = this.getClass()
                .getClassLoader()
                .getResource(fileName)
                .toURI();
        final byte[] bytes = Files.readAllBytes(Paths.get(testFile));
        return new String(bytes);
    }

    @Test
    public void shouldMakeForSimpleParameters() throws IOException, URISyntaxException {

        final ViceSpec viceSpec = new ViceSpec();

        class Appender {
            String append(String first, String second) {
                final String result = first + " " + second;
                viceSpec.clampMethod(this, "append", result, first, second);
                return result;
            }
        }

        new Appender().append("oll", "eh");

        final String vice = viceFactory.make(viceSpec);
        assertEquals(getExpectedVice("AppenderVice.java"), vice);
    }
}
