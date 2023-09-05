package fr.insalyon.creatis.grida.client;

import fr.insalyon.creatis.grida.common.Communication;
import fr.insalyon.creatis.grida.common.StandaloneCommunication;
import fr.insalyon.creatis.grida.server.StandaloneServer;

import java.io.File;

/*
    This just uses a new communication without Socket for a client to exchange with the server implementation

    The StandaloneCommunication handles everything :
    - When the client calls sendEndOfMessage, StandaloneCommunication call the Server Executor synchronously
    - StandaloneCommunication resets its message for the Executor to write its response in
    - When the executor has finished, the client gets the response with getMessage
 */
public class StandaloneGridaClient extends GRIDAClient {

    public StandaloneGridaClient(String proxyPath, File confFile) {
        super(null, -1, proxyPath);
        new StandaloneServer(confFile); // mandatory to configure stuff
    }

    @Override
    protected Communication getCommunication() throws GRIDAClientException {
        return new StandaloneCommunication();
    }
}
