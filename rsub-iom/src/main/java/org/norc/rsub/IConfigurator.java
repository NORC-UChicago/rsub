package org.norc.rsub;

import com.sas.services.connection.ConnectionFactoryConfiguration;
import com.sas.services.connection.Credential;

public interface IConfigurator {
  Credential getCredential();

  void setCredential(Credential credential);

  ConnectionFactoryConfiguration getConfiguration();

  void setConfiguration(ConnectionFactoryConfiguration configuration);
}
