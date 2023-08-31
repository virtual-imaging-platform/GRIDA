package fr.insalyon.creatis.grida.server;

import fr.insalyon.creatis.grida.server.dao.DAOException;
import fr.insalyon.creatis.grida.server.dao.DAOFactory;
import fr.insalyon.creatis.grida.server.execution.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class StandaloneServer extends Server {

    private static final Logger logger = Logger.getLogger(StandaloneServer.class);

    private static boolean initDone = false;

    @Override
    public void init () throws DAOException {
        // to be done only once
        if (initDone) {
            return;
        }

        this.initConfig();
        logger.info("Starting standalone GRIDA Server");
        this.initPools();
    }

}
