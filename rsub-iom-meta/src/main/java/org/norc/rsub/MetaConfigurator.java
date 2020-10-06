package org.norc.rsub;

import com.sas.meta.SASOMI.IOMI;
import com.sas.meta.SASOMI.IOMIHelper;
import com.sas.metadata.remote.CMetadata;
import com.sas.metadata.remote.MdFactory;
import com.sas.metadata.remote.MdFactoryImpl;
import com.sas.metadata.remote.MdOMIUtil;
import com.sas.metadata.remote.MdOMRConnection;
import com.sas.services.connection.ConnectionFactoryConfiguration;
import com.sas.services.connection.ConnectionFactoryInterface;
import com.sas.services.connection.ConnectionFactoryManager;
import com.sas.services.connection.ConnectionInterface;
import com.sas.services.connection.omr.OMRConnectionFactoryConfiguration;

/**
 * MetaConfigurator uses a connection to a SAS Metadata Server to collect
 * subsequent connection information about the available Workspace servers.
 */
@SuppressWarnings("unused")
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
