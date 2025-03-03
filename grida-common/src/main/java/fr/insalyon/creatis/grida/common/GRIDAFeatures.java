package fr.insalyon.creatis.grida.common;

public class GRIDAFeatures {
    public boolean hasCache;
    public boolean hasPool;
    public boolean hasZombie;

    public GRIDAFeatures(boolean hasCache, boolean hasPool, boolean hasZombie) {
        this.hasCache = hasCache;
        this.hasPool = hasPool;
        this.hasZombie = hasZombie;
    }
}
