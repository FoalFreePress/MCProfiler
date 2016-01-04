package org.sweetiebelle.mcprofiler;

public abstract class Settings {

    public String dbDatabase;
    public String dbHost;
    public String dbPass;
    public String dbPort;
    public String dbPrefix;
    public String dbUser;
    public boolean stackTraces;
    public boolean showQuery;
    public boolean useDebug;
    public boolean recOnJoin;

    public abstract void reloadSettings();
}