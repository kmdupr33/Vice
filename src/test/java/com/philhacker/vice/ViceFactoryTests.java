package com.philhacker.vice;

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

    @Test
    public void shouldMakeForSimpleParameters() throws URISyntaxException, IOException {

        ViceFactory viceFactory = new ViceFactory();
        final ViceSpec viceSpec = new ViceSpec();

        class Reverser {
            String reverse(String toReverse) {
                final String result = new StringBuilder(toReverse).reverse().toString();
                viceSpec.clampMethod(this, result, toReverse);
                return result;
            }
        }

        new Reverser().reverse("hello");

        String vice = viceFactory.make(viceSpec);

        final URI testFile = this.getClass()
                .getClassLoader()
                .getResource("ReverserVice.java")
                .toURI();
        final byte[] bytes = Files.readAllBytes(Paths.get(testFile));
        assertEquals(new String(bytes), vice);
    }
}
