package org.norc.rsub;

import com.sas.iom.SAS.ILanguageServicePackage.CarriageControl;
import com.sas.iom.SAS.ILanguageServicePackage.LineType;
import com.sas.iom.SASEvents.ILanguageLogEventOperations;

public class LanguageLogEventOperations implements ILanguageLogEventOperations {
  private final IDestination log;
  private final IDestination list;

  protected LanguageLogEventOperations(IDestination log, IDestination list) {
    this.log = log;
    this.list = list;
  }

  @Override
  public void AbortLogLines(
      CarriageControl[] carriageControls, LineType[] lineTypes, String[] strings) {}

  @Override
  public void AbortListLines(
      CarriageControl[] carriageControls, LineType[] lineTypes, String[] strings) {}

  @Override
  public void StepLogLines(
      CarriageControl[] carriageControls, LineType[] lineTypes, String[] strings) {
    log.write(lineTypes, strings);
  }

  @Override
  public void StepListLines(
      CarriageControl[] carriageControls, LineType[] lineTypes, String[] strings) {
    list.write(lineTypes, strings);
  }
}
