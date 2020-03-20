package org.norc.rsub;

import com.sas.services.connection.ConnectionFactoryConfiguration;
import com.sas.services.connection.Credential;

public abstract class Configurator {
  private Credential credential;
  private ConnectionFactoryConfiguration configuration;

  public Credential getCredential() {
    return credential;
  }

  public void setCredential(Credential credential) {
    this.credential = credential;
  }

  public ConnectionFactoryConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(ConnectionFactoryConfiguration configuration) {
    this.configuration = configuration;
  }
}
