package org.norc.rsub;

import com.sas.iom.orb.SASURI;
import com.sas.services.connection.*;

public class ManualConfigurator extends Configurator {
  public ManualConfigurator() {
    Credential credential = createCredential();
    setCredential(credential);

    String stringUri = System.getProperty("rsub.serverUri");
    if (stringUri == null) {
      throw new IllegalArgumentException("rsub.serverUri property is required!");
    }
    SASURI uri = SASURI.create(stringUri);
    Server server = BridgeServer.fromURI(uri);
    ManualConnectionFactoryConfiguration configuration =
        new ManualConnectionFactoryConfiguration(server);
    setConfiguration(configuration);
  }

  public ManualConfigurator(Server server) {
    this(new Server[] {server});
  }

  public ManualConfigurator(Server[] servers) {
    this(new FailoverCluster(servers));
  }

  public ManualConfigurator(Cluster cluster) {
    this(new Cluster[] {cluster});
  }

  public ManualConfigurator(Cluster[] clusters) {
    Credential credential = createCredential();
    setCredential(credential);

    ManualConnectionFactoryConfiguration configuration =
        new ManualConnectionFactoryConfiguration(clusters);
    setConfiguration(configuration);
  }

  private Credential createCredential() {
    Credential credential;
    String username = System.getProperty("rsub.username");
    if (username != null) {
      String password = System.getProperty("rsub.password");
      String domain = System.getProperty("rsub.authdomain", "DefaultAuth");
      credential = new PasswordCredential(username, password, domain);
    } else {
      credential = new SecurityPackageCredential();
    }
    return credential;
  }
}
