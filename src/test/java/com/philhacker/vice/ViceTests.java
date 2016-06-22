package com.philhacker.vice;

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
                = "/Users/mattdupree/Developer/Vice/src/test/java/";
        final Path testFilesPath = Paths.get(pathToGeneratedCharacterizationTest);
        vice.make(testFilesPath, ViceMaker.class);
        final Path generatedFilePath = Paths.get(testFilesPath.toFile().getPath() + "/com/philhacker/vice/ReverserCharacterization.java");
        final String generatedFile = new String(Files.readAllBytes(generatedFilePath));
        //noinspection ConstantConditions
        final Path expectedFilePath = Paths.get(this.getClass().getClassLoader().getResource("SimpleTestGen.java").toURI());
        final String expectedFile = new String(Files.readAllBytes(expectedFilePath));

        Files.delete(generatedFilePath);
        assertEquals(expectedFile, generatedFile);
    }
}
