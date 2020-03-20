package org.norc.rsub;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppUtilsTest {

  @Test
  public void shouldAddQuotesWhenNone() {
    String input = "this is a string";
    String output = AppUtils.addQuotes(input);
    assertEquals("\"this is a string\"", output);
  }

  @Test
  public void shouldNotAddQuotesWhenPresent() {
    String input = "\"this is a string\"";
    String output = AppUtils.addQuotes(input);
    assertEquals(input, output);
    String inputSingle = "'this is a string'";
    String outputSingle = AppUtils.addQuotes(inputSingle);
    assertEquals(inputSingle, outputSingle);
  }

  @Test
  public void shouldEscapeEmbeddedQuotes() {
    String input = "this is a \"string\"";
    String output = AppUtils.addQuotes(input);
    assertEquals("\"this is a \"\"string\"\"\"", output);
  }
}