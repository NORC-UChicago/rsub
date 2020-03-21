package org.norc.rsub;

import com.sas.iom.SAS.*;
import com.sas.iom.SASEvents.ILanguageEvents_1_1Helper;
import com.sas.iom.SASEvents.ILanguageEvents_1_1POATie;
import com.sas.iom.SASEvents.ILanguageLogEventHelper;
import com.sas.iom.SASEvents.ILanguageLogEventPOATie;
import com.sas.iom.SASIOMDefs.GenericError;
import com.sas.iom.orb.EventUtil;
import com.sas.services.connection.*;

public class IOMAdapter {
  private final IConfigurator configurator;
  private final IDestination log;
  private final IDestination print;

  private ConnectionInterface cx;
  private IWorkspace workspace;
  private ILanguageService_1_1 languageService;

  private int languageEventsHandle;
  private int languageLogEventHandle;

  protected int sysrc = 0;
  protected boolean complete = false;

  public IOMAdapter(IConfigurator configurator) {
    this(configurator, new NullDestination());
  }

  public IOMAdapter(IConfigurator configurator, IDestination log) {
    this(configurator, log, new NullDestination());
  }

  public IOMAdapter(IConfigurator configurator, IDestination log, IDestination print) {
    this.configurator = configurator;
    this.log = log;
    this.print = print;
  }

  public void connect() throws ConnectionFactoryException {
    ConnectionFactoryManager manager = new ConnectionFactoryManager();
    ConnectionFactoryConfiguration configuration = configurator.getConfiguration();
    ConnectionFactoryInterface factory = manager.getFactory(configuration);
    cx = factory.getConnection(configurator.getCredential());
    narrowObjects();
    addInterceptors();
  }

  private void narrowObjects() {
    org.omg.CORBA.Object cxObject = cx.getObject();

    workspace = IWorkspaceHelper.narrow(cxObject);
    languageService = ILanguageService_1_1Helper.narrow(workspace.LanguageService());
    languageService.Async(true);
    languageService.FlushLogPerStep(true);
  }

  private void addInterceptors() {
    LanguageEventsOperations languageEventsOperations = new LanguageEventsOperations(this);
    ILanguageEvents_1_1POATie languageEventsServant =
        new ILanguageEvents_1_1POATie(languageEventsOperations);
    languageEventsHandle =
        EventUtil.advise(languageService, ILanguageEvents_1_1Helper.id(), languageEventsServant);

    LanguageLogEventOperations languageLogEventOperations =
        new LanguageLogEventOperations(this.log, this.print);
    ILanguageLogEventPOATie languageLogEventServant =
        new ILanguageLogEventPOATie(languageLogEventOperations);
    languageLogEventHandle =
        EventUtil.advise(languageService, ILanguageLogEventHelper.id(), languageLogEventServant);
  }

  public void close() throws GenericError {
    EventUtil.unadvise(languageService, ILanguageEvents_1_1Helper.id(), languageEventsHandle);
    EventUtil.unadvise(languageService, ILanguageLogEventHelper.id(), languageLogEventHandle);
    workspace.Close();
    cx.close();
  }

  public void submit(String pgm) throws GenericError {
    this.complete = false;
    languageService.Submit(pgm);
  }

  public void submit(String[] pgm) throws GenericError {
    this.complete = false;
    languageService.SubmitLines(pgm);
  }

  protected void markComplete(int sysrc) {
    this.sysrc = Math.max(this.sysrc, sysrc);
    this.complete = true;
  }

  public void resetLogLineNumbers() throws GenericError {
    languageService.ResetLogLineNumbers();
  }
}
