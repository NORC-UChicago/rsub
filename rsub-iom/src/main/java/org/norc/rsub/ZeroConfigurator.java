package org.norc.rsub;

import com.sas.services.connection.Credential;
import com.sas.services.connection.ManualConnectionFactoryConfiguration;
import com.sas.services.connection.SecurityPackageCredential;
import com.sas.services.connection.ZeroConfigWorkspaceServer;

public class ZeroConfigurator extends Configurator {
  public ZeroConfigurator() {
    Credential credential = new SecurityPackageCredential();
    setCredential(credential);

    ZeroConfigWorkspaceServer server = new ZeroConfigWorkspaceServer();
    String spn = System.getProperty("rsub.spn", "SAS/localhost");
    server.setSPN(spn);
    setConfiguration(new ManualConnectionFactoryConfiguration(server));
  }
}
