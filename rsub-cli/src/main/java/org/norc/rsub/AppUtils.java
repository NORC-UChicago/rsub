package org.norc.rsub;

class AppUtils {
  private static final String DOUBLE_QUOTE = "\"";
  private static final String SINGLE_QUOTE = "'";

  static String addQuotes(String s) {
    if ((s.startsWith(DOUBLE_QUOTE) && s.endsWith(DOUBLE_QUOTE)) || (s.startsWith(SINGLE_QUOTE) && s.endsWith(SINGLE_QUOTE))) {
      return s;
    } else {
      return String.format("\"%s\"", s.replace("\"", "\"\""));
    }
  }
}
