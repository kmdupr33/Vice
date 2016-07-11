package com.philhacker.vice;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by mattdupree on 7/11/16.
 */
public class InstallerTests {

    private Installer installer;

    @Before
    public void makeInstaller() {
        installer = new Installer();
    }

    @Test
    public void installOnMethodInvocation() {
        class Reverser {

            String reverse(String toReverse) {
                final String result = new StringBuilder(toReverse).reverse().toString();
                installer.onMethod(result, toReverse);
                return result;
            }
        }

        new Reverser().reverse("hello");

        assertEquals(new Invocation("olleh", "hello"), installer.getInvocation());
    }
}
