package fr.insalyon.creatis.grida.client;

import java.io.File;

/*
Zombie stuff are not used
 */
public class StandaloneGridaClientMain extends GRIDAClientMain {

    public static void main(String[] args) {
        new StandaloneGridaClientMain(args).run();
    }

    public StandaloneGridaClientMain(String[] args) {
        super(args);
    }

    @Override
    protected void initClient() {
        this.client = new StandaloneGridaClient(options.proxy, new File(options.confFile));
        this.poolClient = new StandaloneGridaPoolClient(options.proxy, new File(options.confFile));
    }
}
