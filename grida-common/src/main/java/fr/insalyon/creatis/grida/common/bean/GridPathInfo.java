package fr.insalyon.creatis.grida.common.bean;

public class GridPathInfo {
    private boolean doesExist;
    private GridData.Type type; // type is undefined and can be null when doesExist is false

    public GridPathInfo(boolean exist, GridData.Type type) {
        this.doesExist = exist;
        this.type = type;
    }

    public boolean exist() {
        return doesExist;
    }
    public GridData.Type getType() {
        return type;
    }
}
