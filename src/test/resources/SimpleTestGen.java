package com.philhacker.vice;

import static junit.framework.TestCase.assertEquals;

import java.lang.String;
import org.junit.Test;

public class ReverserCharacterization {
  @Test
  public void characterizeReverse() {
    Reverser reverser = new Reverser();
    final String result = reverser.reverse("hello");
    assertEquals("olleh", result);
  }
}
