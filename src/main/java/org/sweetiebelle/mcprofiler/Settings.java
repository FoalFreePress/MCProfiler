package org.sweetiebelle.mcprofiler;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

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
    private final MCProfilerPlugin plugin;
    private FileConfiguration config;

    Settings(final MCProfilerPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        readSettings();
    }

    /**
     * Reads settings
     *
     * @param pConfig
     *            the config
     */
    protected void readSettings() {
        stackTraces = config.getBoolean("general.printStackTraces");
        showQuery = config.getBoolean("general.showquery");
        useDebug = config.getBoolean("general.debug");
        recOnJoin = config.getBoolean("general.recursivePlayerJoin");
        dbHost = config.getString("database.host");
        dbPort = config.getString("database.port");
        dbUser = config.getString("database.username");
        dbPass = config.getString("database.password");
        dbDatabase = config.getString("database.database");
        dbPrefix = config.getString("database.prefix");
    }

    /**
     * Reloads settings
     */
    public void reloadSettings() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        readSettings();
    }
}