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

    static ViceSpec viceSpec = new ViceSpec();

    @Before
    public void makeFactory() {
        viceFactory = new ViceFactory();
        viceSpec = new ViceSpec();
    }

    @Test
    public void shouldMakeForSimpleParameter() throws URISyntaxException, IOException {

        viceFactory = new ViceFactory();

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

        new Appender().append("oll", "eh");

        final String vice = viceFactory.make(viceSpec);
        assertEquals(getExpectedVice("AppenderVice.java"), vice);
    }

    @Test
    public void shouldMakeForComplexReturnValue() throws IOException, URISyntaxException {

        new PersonMaker().make("Bill");
        final String vice = viceFactory.make(viceSpec);

        assertEquals(getExpectedVice("PersonMakerVice.java"), vice);
    }

    static class Reverser {
        String reverse(String toReverse) {
            final String result = new StringBuilder(toReverse).reverse().toString();
            viceSpec.clampMethod(this, "reverse", result, toReverse);
            return result;
        }
    }

    static class Appender {

        String append(String first, String second) {
            final String result = first + " " + second;
            viceSpec.clampMethod(this, "append", result, first, second);
            return result;
        }
    }
    static class PersonMaker {

        class Person {
            private final String firstName;

            Person(String firstName) {
                this.firstName = firstName;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Person person = (Person) o;

                return firstName != null ? firstName.equals(person.firstName) : person.firstName == null;

            }

            @Override
            public int hashCode() {
                return firstName != null ? firstName.hashCode() : 0;
            }
        }

        Person make(String name) {
            final Person person = new Person(name);
            viceSpec.clampMethod(this, "make", person, name);
            return person;
        }
    }
}
