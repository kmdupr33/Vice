package com.philhacker.vice;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by mattdupree on 6/21/16.
 */
public class ViceTests {
    @Test
    public void simpleTest() {

        final String source =
                "import com.philhacker.vice.annotations.Clamp;\n" +
                "import com.philhacker.vice.annotations.ViceFor;\n" +
                "public class Reverser {\n" +
                "    public String reverse(String string) {\n" +
                "        return new StringBuilder(string).reverse().toString();\n" +
                "    }\n" +
                "\n" +
                "    @ViceFor(Reverser.class)\n" +
                "    public static class ViceMaker {\n" +
                "\n" +
                "        @ViceFor(Reverser.class)\n" +
                "        @Clamp(\"reverse\")\n" +
                "        public void clampReverse() {\n" +
                "            Reverser reverser = new Reverser();\n" +
                "            reverser.reverse(\"hello\");\n" +
                "        }\n" +
                "    }\n" +
                "}";
        final JavaFileObject sourceFile = JavaFileObjects.forSourceString("Reverser", source);
        assertAbout(javaSource())
                .that(sourceFile)
                .processedWith(new ViceProcessor())
                .compilesWithoutError();

    }
}
