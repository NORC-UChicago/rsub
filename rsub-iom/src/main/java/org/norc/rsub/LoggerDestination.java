package org.norc.rsub;

import static com.sas.iom.SAS.ILanguageServicePackage.LineType.LineTypeError;
import static com.sas.iom.SAS.ILanguageServicePackage.LineType.LineTypeWarning;

import com.sas.iom.SAS.ILanguageServicePackage.LineType;
import org.apache.logging.log4j.Logger;

public class LoggerDestination implements IDestination {
  private Logger logger;

  public LoggerDestination() {}

  public LoggerDestination(Logger logger) {
    setOut(logger);
  }

  @Override
  public void setOut(Object out) {
    setOut((Logger) out);
  }

  public void setOut(Logger logger) {
    this.logger = logger;
  }

  @Override
  public void write(LineType[] lineTypes, String[] strings) {
    for (int i = 0; i < strings.length; i++) {
      LineType type = lineTypes[i];
      if (LineTypeError.equals(type)) {
        logger.error(strings[i]);
      } else if (LineTypeWarning.equals(type)) {
        logger.warn(strings[i]);
      } else {
        logger.debug(strings[i]);
      }
    }
  }

  @Override
  public void close() {
    // ignored
  }
}
