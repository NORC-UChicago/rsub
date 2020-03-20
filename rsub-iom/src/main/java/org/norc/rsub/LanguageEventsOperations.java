package org.norc.rsub;

import com.sas.iom.SASEvents.ILanguageEvents_1_1Operations;

public class LanguageEventsOperations implements ILanguageEvents_1_1Operations {
  private final IOMAdapter adapter;

  public LanguageEventsOperations(IOMAdapter adapter) {
    this.adapter = adapter;
  }

  @Override
  public void GlobalstmtComplete(String s) {}

  @Override
  public void GlobalstmtError(String s) {}

  @Override
  public void ProcStart(String s) {}

  @Override
  public void SubmitComplete(int i) {
    adapter.markComplete(i);
  }

  @Override
  public void ProcComplete(String s) {}

  @Override
  public void DatastepStart() {}

  @Override
  public void DatastepComplete() {}

  @Override
  public void StepError() {}
}
