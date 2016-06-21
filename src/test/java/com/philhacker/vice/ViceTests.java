package com.philhacker.vice;

import com.philhacker.vice.annotations.Vice;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by mattdupree on 6/21/16.
 */
public class ViceTests {
    @Test
    public void simpleTest() throws URISyntaxException, IOException {

        final Vice vice = new Vice();
        final String pathToGeneratedCharacterizationTest
                = "/Users/mattdupree/Developer/Vice/src/test/java/com/philhacker/vice/ReverserCharacterizations.java";
        final Path path = Paths.get(pathToGeneratedCharacterizationTest);
        vice.make(path.subpath(0, path.getNameCount() - 1).toFile().getPath(), ViceMaker.class);
        final String generatedFile = new String(Files.readAllBytes(path));
        //noinspection ConstantConditions
        final Path expectedFilePath = Paths.get(this.getClass().getClassLoader().getResource("SimpleTestGen.java").toURI());
        final String expectedFile = new String(Files.readAllBytes(expectedFilePath));

        assertEquals(expectedFile, generatedFile);
    }
}
