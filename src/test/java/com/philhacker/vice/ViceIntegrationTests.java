package com.philhacker.vice;

import com.philhacker.vice.recordingobjects.ByteBuddyRecordingObjectFactory;
import com.philhacker.vice.regressiontestwriter.JavaPoetRegressionTestWriter;
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
public class ViceIntegrationTests {

    @Test
    public void simpleTest() throws URISyntaxException, IOException {

        final String pathToGeneratedCharacterizationTest
                = "/Users/mattdupree/Developer/Vice/src/test/java/";
        final Path testFilesPath = Paths.get(pathToGeneratedCharacterizationTest);
        final Vice vice = new Vice(new JavaPoetRegressionTestWriter(testFilesPath), new ByteBuddyRecordingObjectFactory());
        vice.make(ViceMaker.class);
        final Path generatedFilePath = Paths.get(testFilesPath.toFile().getPath() + "/com/philhacker/vice/ReverserCharacterization.java");
        final String generatedFile = new String(Files.readAllBytes(generatedFilePath));
        //noinspection ConstantConditions
        final Path expectedFilePath = Paths.get(this.getClass().getClassLoader().getResource("SimpleTestGen.java").toURI());
        final String expectedFile = new String(Files.readAllBytes(expectedFilePath));

        Files.delete(generatedFilePath);
        assertEquals(expectedFile, generatedFile);
    }
}
