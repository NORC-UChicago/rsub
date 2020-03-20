package org.norc.rsub;

import com.sas.services.connection.ConnectionFactoryException;
import com.sas.services.connection.SecurityPackageCredential;
import com.sas.services.connection.XMLConfigurationBuilder;

import java.io.File;

public class XMLConfigurator extends Configurator {
  public XMLConfigurator() throws ConnectionFactoryException {
    String serverInfo = System.getProperty("rsub.serverInfo", "./conf/serverInfo.xml");
    File serverInfoFile = new File(serverInfo);

    String userInfo = System.getProperty("rsub.userInfo");

    XMLConfigurationBuilder builder;
    if (userInfo == null) {
      builder = new XMLConfigurationBuilder(serverInfoFile);
      setCredential(new SecurityPackageCredential());
    } else {
      File userInfoFile = new File(userInfo);
      builder = new XMLConfigurationBuilder(serverInfoFile, userInfoFile);
      String domain = System.getProperty("rsub.domain", "DefaultAuth");
      setCredential(builder.getCredentialForDomain(domain));
    }
    setConfiguration(builder.getConfiguration());
  }
}
