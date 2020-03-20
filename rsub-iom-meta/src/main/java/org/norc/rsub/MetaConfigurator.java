package org.norc.rsub;

import com.sas.meta.SASOMI.IOMI;
import com.sas.meta.SASOMI.IOMIHelper;
import com.sas.metadata.remote.*;
import com.sas.services.connection.*;
import com.sas.services.connection.omr.OMRConnectionFactoryConfiguration;

public class MetaConfigurator extends Configurator {
  public MetaConfigurator() throws Exception {
    String serverLogicalName =
        System.getProperty("rsub.serverLogicalName", "SASApp - Logical Workspace Server");

    XMLConfigurator xmlConfigurator = new XMLConfigurator();
    setCredential(xmlConfigurator.getCredential());

    ConnectionFactoryConfiguration metaConfiguration = xmlConfigurator.getConfiguration();
    ConnectionFactoryManager manager = new ConnectionFactoryManager();
    ConnectionFactoryInterface factory = manager.getFactory(metaConfiguration);
    ConnectionInterface cxMeta = factory.getConnection(getCredential());
    org.omg.CORBA.Object cxMetaObject = cxMeta.getObject();
    IOMI iomi = IOMIHelper.narrow(cxMetaObject);

    String reposid = getFoundationRepository(iomi);
    OMRConnectionFactoryConfiguration configuration =
        new OMRConnectionFactoryConfiguration(iomi, reposid, serverLogicalName);

    setConfiguration(configuration);
  }

  private String getFoundationRepository(IOMI iomi) throws Exception {
    MdFactory factory = new MdFactoryImpl();
    MdOMRConnection connection = factory.getConnection();
    connection.setCMRHandle(iomi);
    MdOMIUtil omiUtil = factory.getOMIUtil();
    CMetadata foundation = omiUtil.getFoundationRepository();
    return foundation.getId();
  }
}
