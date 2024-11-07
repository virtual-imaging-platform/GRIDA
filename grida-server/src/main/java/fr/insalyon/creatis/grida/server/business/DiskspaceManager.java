package fr.insalyon.creatis.grida.server.business;

import java.io.File;

import fr.insalyon.creatis.grida.server.Configuration;

public class DiskspaceManager {
    
    public long getFreeSpace() {
        return new File(".").getFreeSpace();
    }

    public long getTotalSpace() {
        return new File(".").getTotalSpace();
    }

    public double getMinAvailableDiskSpace() {
        return Configuration.getInstance().getMinAvailableDiskSpace();
    }
}
