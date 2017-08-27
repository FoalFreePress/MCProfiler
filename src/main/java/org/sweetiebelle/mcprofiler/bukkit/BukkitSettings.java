package org.sweetiebelle.mcprofiler.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.sweetiebelle.mcprofiler.Settings;

public class BukkitSettings extends Settings {

    private final MCProfilerPlugin plugin;
    private FileConfiguration config;

    BukkitSettings(final MCProfilerPlugin plugin) {
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
    @Override
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
    @Override
    public void reloadSettings() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        readSettings();
    }
}
