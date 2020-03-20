package org.norc.rsub;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.io.FileUtils;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

@Command(
    name = "rsub",
    sortOptions = false,
    separator = " ",
    requiredOptionMarker = '*',
    versionProvider = org.norc.rsub.ManifestVersionProvider.class,
    synopsisHeading = "@|bold Usage|@:%n%n",
    header = {
      "@|yellow .______          _______. __    __  .______|@",
      "@|yellow |   _  \\        /       ||  |  |  | |   _  \\|@",
      "@|yellow |  |_)  |      |   (----`|  |  |  | |  |_)  ||@",
      "@|yellow |      /        \\   \\    |  |  |  | |   _  <|@",
      "@|yellow |  |\\  \\----.----)   |   |  `--'  | |  |_)  ||@",
      "@|yellow | _| `._____|_______/     \\______/  |______/|@",
      ""
    },
    description = {
      "",
      "@|bold RSUB|@, a Command-Line Interface for @|blue,bold SAS|@ 9 Environments"
    },
    optionListHeading = "@|bold %nOptions|@:%n",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true,
    showDefaultValues = true,
    exitCodeListHeading = "@|bold %nExit Codes|@:%n",
    exitCodeList = {
      " @|yellow 0|@: Successful program execution",
      " @|yellow 1|@: SAS issued warning(s)",
      " @|yellow 2|@: SAS issues error(s)"
    })
public class App implements Callable<Integer> {
  @ArgGroup(validate = false, heading = "@|blue,bold SAS|@ System Options Section%n", order = 1)
  SASOptionSection sasOptions;

  static class SASOptionSection {
    @Option(
        names = "-sysin",
        required = true,
        paramLabel = "<file>",
        description = "specifies a file containing a @|blue,bold SAS|@ program")
    File sysin = null;

    @Option(
        names = "-log",
        paramLabel = "<file>",
        description = "specifies the destination for the @|blue,bold SAS|@ log")
    File log = null;

    @Option(
        names = "-print",
        paramLabel = "<file>",
        description = "specifies the destination for the @|blue,bold SAS|@ listing destination")
    File print = null;

    @Option(
        names = "-sysparm",
        paramLabel = "<characters>",
        description =
            "specifies a character string that can be passed to @|blue,bold SAS|@ programs")
    String sysparm = null;

    @Option(
        names = "-set",
        paramLabel = "<Variable=Value>",
        showDefaultValue = Visibility.NEVER,
        description = "defines an environment variable.  Can be used multiple times.")
    Map<String, String> sets = new HashMap<>();
  }

  @ArgGroup(validate = false, heading = "", order = 2)
  RsubOptionSection rsubOptions = new RsubOptionSection();

  static class RsubOptionSection {
    @Option(
        names = "--config-class",
        paramLabel = "<configurator>",
        description = "the @|yellow,bold rsub|@ configuration class",
        defaultValue = "org.norc.rsub.XMLConfigurator")
    Class configuratorClass = org.norc.rsub.XMLConfigurator.class;

    @Option(
        names = "--encoding",
        description = "encoding of @|italic sysin|@ file",
        defaultValue = "UTF-8")
    String encoding = "UTF-8";
  }

  /**
   * Main method for rsub-cli.
   *
   * @param args command-line arguments
   */
  public static void main(String... args) {
    CommandLine commandLine = new CommandLine(new App());
    AnsiConsole.systemInstall();
    commandLine.setUsageHelpLongOptionsMaxWidth(30);
    int sysrc = commandLine.execute(args);
    int exitCode = (sysrc > 4) ? 2 : ((sysrc == 4) ? 1 : 0);
    AnsiConsole.systemUninstall();
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    if (sasOptions == null || sasOptions.sysin == null)
      throw new IllegalArgumentException("Missing required option '-sysin <file>'");

    Configurator configurator = (Configurator) rsubOptions.configuratorClass.newInstance();
    IOMAdapter adapter = new IOMAdapter(configurator);
    adapter.connect();

    List<String> header = getAutoexec();
    adapter.submit(header.toArray(new String[0]));

    do {
      Thread.sleep(10);
    } while (!adapter.complete);

    adapter.resetLogLineNumbers();

    List<String> program = FileUtils.readLines(sasOptions.sysin, rsubOptions.encoding);
    adapter.submit(program.toArray(new String[0]));

    do {
      Thread.sleep(100);
    } while (!adapter.complete);

    adapter.close();

    return adapter.sysrc;
  }

  private List<String> getAutoexec() {
    List<String> header = new ArrayList<>();
    header.add(String.format("%%let _SASPROGRAMFILE=%s;\n\n", sasOptions.sysin.getAbsolutePath()));
    if (sasOptions.sysparm != null && !sasOptions.sysparm.isEmpty()) {
      header.add(String.format("options sysparm=%s;\n\n", AppUtils.addQuotes(sasOptions.sysparm)));
    }
    sasOptions.sets.forEach(
        (var, val) ->
            header.add(String.format("options set=%s %s;\n", var, AppUtils.addQuotes(val))));
    header.add("\n\n");
    return header;
  }
}
