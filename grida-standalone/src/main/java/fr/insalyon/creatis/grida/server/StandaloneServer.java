package fr.insalyon.creatis.grida.server;

import fr.insalyon.creatis.grida.common.GRIDAFeatures;
import fr.insalyon.creatis.grida.server.dao.DAOException;
import fr.insalyon.creatis.grida.server.dao.DAOFactory;
import fr.insalyon.creatis.grida.server.execution.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

public class StandaloneServer extends Server {

    private static final Logger logger = Logger.getLogger(StandaloneServer.class);

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
        PropertyConfigurator.configure(Server.class.getClassLoader().getResource("gridastandaloneLog4j.properties"));
        Configuration config = Configuration.getInstance(confFile, new GRIDAFeatures(false, false, false));
    }
}
