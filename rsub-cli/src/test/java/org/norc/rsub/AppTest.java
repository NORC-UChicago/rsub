package org.norc.rsub;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Test;
import picocli.CommandLine;

public class AppTest {

  @Test
  public void shouldPrintVersion() {
    App app = new App();
    CommandLine cmd = new CommandLine(app);

    StringWriter sw = new StringWriter();
    cmd.setOut(new PrintWriter(sw));

    int exitCode = cmd.execute("-V");
    assertEquals(0, exitCode);
    assertEquals("rsub version \"0.1\"" + System.lineSeparator(), sw.toString());
  }

  @Test
  public void shouldRunExampleZero() {
    App app = new App();
    CommandLine cmd = new CommandLine(app);

    int exitCode = cmd.execute("-sysin", getResourceAsPath("test.sas"),"--config-class","org.norc.rsub.ZeroConfigurator");
    assertEquals(0, exitCode);
  }

  private String getResourceAsPath(String name) {
    return AppTest.class.getClassLoader().getResource(name).getPath();
  }
}