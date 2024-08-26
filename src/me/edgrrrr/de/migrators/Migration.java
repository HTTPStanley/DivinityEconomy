package me.edgrrrr.de.migrators;

import me.edgrrrr.de.DEPlugin;

public abstract class Migration {
    private final DEPlugin main;
    private final String VERSION;
    private final String NEXT_VERSION;


    public Migration(DEPlugin main, String version, String nextVersion) {
        this.main = main;
        this.VERSION = version;
        this.NEXT_VERSION = nextVersion;
    }


    protected abstract void migrate();


    public DEPlugin getMain() {
        return this.main;
    }


    public String getVersion() {
        return this.VERSION;
    }


    public String getNextVersion() {
        return this.NEXT_VERSION;
    }
}
