package fr.insalyon.creatis.grida.server.business;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import fr.insalyon.creatis.grida.server.Configuration;
import fr.insalyon.creatis.grida.server.operation.LocalOperations;

public class DiskspaceManager {

    private final static Logger logger = Logger.getLogger(LocalOperations.class);

    public long getFreeSpace() {
        return new File(".").getFreeSpace();
    }

    public long getTotalSpace() {
        return new File(".").getTotalSpace();
    }

    public double getMinAvailableDiskSpace() {
        return Configuration.getInstance().getMinAvailableDiskSpace();
    }

    public boolean isTransferable(long sizeToAdd) {
        long freeSpace = getFreeSpace();
        long totalSpace = getTotalSpace();
        boolean result = freeSpace - sizeToAdd > totalSpace * getMinAvailableDiskSpace();

        if ( ! result) {
            logger.warn("Size limit reached, size needed : " + ((int) sizeToAdd / 1024 / 1024) + " MB.");
        }
        return result;
    }

    public static void deleteQuietly(File file) {
        if ( ! FileUtils.deleteQuietly(file)) {
            logger.warn("Failed to delete quietly file : " + file.getPath());
        }
    }
}
