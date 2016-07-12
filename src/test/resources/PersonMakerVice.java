package com.philhacker.vice;

import com.google.gson.Gson;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class PersonMakerVice {

    @Test
    public void clampMake() throws URISyntaxException, IOException {
        InputStream resource = this.getClass().getClassLoader().getResource("PersonViceMaker.json").openStream();
        ViceFactoryTests.PersonMaker.Person personMakerMakeResult = new Gson().fromJson(new InputStreamReader(resource),
                                                                                        ViceFactoryTests.PersonMaker.Person.class);
        ViceFactoryTests.PersonMaker personmaker = new ViceFactoryTests.PersonMaker();
        ViceFactoryTests.PersonMaker.Person result = personmaker.make("bill");
        assertEquals(personMakerMakeResult, result);
    }
}
