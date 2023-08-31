package fr.insalyon.creatis.grida.client;

/*
Zombie stuff are not used
 */
public class StandaloneGridaClientMain extends GRIDAClientMain {

    public StandaloneGridaClientMain(String[] args) {
        super(args);
    }

    @Override
    protected void initClient() {
        this.client = new StandaloneGridaClient(options.proxy);
        this.poolClient = new StandaloneGridaPoolClient(options.proxy);
    }
}
