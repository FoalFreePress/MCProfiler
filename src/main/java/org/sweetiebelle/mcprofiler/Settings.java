package org.sweetiebelle.mcprofiler;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {


    public String dbPrefix;
    public boolean recOnJoin;
    private final MCProfiler plugin;
    private FileConfiguration config;
    public boolean printStackTraces;

    Settings(final MCProfiler plugin) {
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
        recOnJoin = config.getBoolean("general.recursivePlayerJoin");
        dbPrefix = config.getString("database.prefix");
        printStackTraces = config.getBoolean("general.printStackTraces");
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