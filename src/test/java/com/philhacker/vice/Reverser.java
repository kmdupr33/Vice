package com.philhacker.vice;

import com.philhacker.vice.annotations.Clamp;
import com.philhacker.vice.annotations.ViceFor;

/**
 * Created by mattdupree on 6/20/16.
 */
public class Reverser {
    public String reverse(String string) {
        return new StringBuilder(string).reverse().toString();
    }

    /**
     * Created by mattdupree on 6/16/16.
     */
    @ViceFor(Reverser.class)
    public static class ViceMaker {

        @ViceFor(Reverser.class)
        @Clamp("reverse")
        public void clampReverse() {
            Reverser reverser = new Reverser();
            reverser.reverse("hello");
        }
    }
}
