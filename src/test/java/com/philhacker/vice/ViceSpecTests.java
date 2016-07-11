package com.philhacker.vice;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by mattdupree on 7/11/16.
 */
public class ViceSpecTests {

    private ViceSpec viceSpec;

    @Before
    public void makeInstaller() {
        viceSpec = new ViceSpec();
    }

    private class Reverser {

        String reverse(String toReverse) {
            final String result = new StringBuilder(toReverse).reverse().toString();
            viceSpec.clampMethod(this, "reverse", result, toReverse);
            return result;
        }
    }

    private class Greeter {
        String greet(String name) {
            final String result = "Hello, " + name;
            viceSpec.clampMethod(this, "greet", result, name);
            return result;
        }
    }

    @Test
    public void shouldInstallOnMethodInvocation() {

        final Reverser reverser = new Reverser();
        reverser.reverse("hello");

        assertEquals(Collections.singletonList(new Invocation(reverser, "olleh", "reverse", "hello")), viceSpec.getInvocations());
    }

    @Test
    public void shouldInstallOnMultipleMethodInvocations() {

        final Reverser reverser = new Reverser();
        reverser.reverse("hello");
        reverser.reverse("goodbye");

        assertEquals(Arrays.asList(new Invocation(reverser, "olleh", "reverse", "hello"),
                                   new Invocation(reverser, "eybdoog", "reverse", "goodbye")), viceSpec.getInvocations());
    }


    @Test
    public void shouldInstallOnMultipleObjects() {

        final Reverser reverser = new Reverser();
        reverser.reverse("hello");
        final Greeter greeter = new Greeter();
        greeter.greet("Billy");

        assertEquals(Arrays.asList(new Invocation(reverser, "olleh", "reverse", "hello"),
                                   new Invocation(greeter, "Hello, Billy", "greet", "Billy")), viceSpec.getInvocations());
    }
}
