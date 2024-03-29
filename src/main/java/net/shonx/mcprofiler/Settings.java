package net.shonx.mcprofiler;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    public String dbPrefix;
    public boolean printStackTraces;
    public boolean recOnJoin;
    public boolean additionalMessages;
    public boolean sendAltsToConsole;
    private FileConfiguration config;
    private MCProfiler plugin;

    Settings(MCProfiler plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        readSettings();
    }

    /**
     * Reloads settings
     */
    public void reloadSettings() {
        plugin.reloadConfig();
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
        additionalMessages = config.getBoolean("general.additionalMessages");
        sendAltsToConsole = config.getBoolean("general.sendAltsToConsole");
    }
}