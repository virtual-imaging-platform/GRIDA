package fr.insalyon.creatis.grida.client;

import fr.insalyon.creatis.grida.common.Communication;
import fr.insalyon.creatis.grida.common.StandaloneCommunication;
import fr.insalyon.creatis.grida.server.StandaloneServer;

/*
See StandaloneGridaClient doc
 */
public class StandaloneGridaPoolClient extends GRIDAPoolClient {

    public StandaloneGridaPoolClient(String proxyPath) {
        super(null, -1, proxyPath);
        new StandaloneServer(); // mandatory to configure stuff
    }


    @Override
    protected Communication getCommunication() throws GRIDAClientException {
        return new StandaloneCommunication();
    }
}
