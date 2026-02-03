package fr.insalyon.creatis.grida.server;

import fr.insalyon.creatis.grida.common.GRIDAFeatures;
import fr.insalyon.creatis.grida.server.dao.DAOException;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandaloneServer extends Server {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneServer.class);

    private static boolean initDone = false;

    public StandaloneServer(File confFile) {
        super(confFile);
    }

    @Override
    public void init (File confFile) throws DAOException {
        // to be done only once
        if (initDone) {
            return;
        }

        this.initConfig(confFile);
        logger.info("Starting standalone GRIDA Server");
        initDone = true;
    }

    @Override
    protected void initConfig(File confFile) {
        Configuration.getInstance(confFile, new GRIDAFeatures(false, false, false));
    }
}
