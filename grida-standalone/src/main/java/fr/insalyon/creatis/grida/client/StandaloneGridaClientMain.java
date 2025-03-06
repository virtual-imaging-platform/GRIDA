package fr.insalyon.creatis.grida.client;

import fr.insalyon.creatis.grida.common.GRIDAFeatures;
import java.io.File;

/*
Zombie stuff are not used
 */
public class StandaloneGridaClientMain extends GRIDAClientMain {

    public static void main(String[] args) {
        new StandaloneGridaClientMain(args).run();
    }

    public StandaloneGridaClientMain(String[] args) {
        super(args, new GRIDAFeatures(false, false, false));
    }

    @Override
    protected void initClient() {
        this.client = new StandaloneGridaClient(options.proxy, new File(options.confFile));
    }
}
