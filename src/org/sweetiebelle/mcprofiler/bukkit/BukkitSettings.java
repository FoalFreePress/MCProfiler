package org.sweetiebelle.mcprofiler.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.sweetiebelle.mcprofiler.Settings;

public class BukkitSettings extends Settings {
    private final MCProfilerPlugin plugin;

    BukkitSettings(final MCProfilerPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        readSettings(plugin.getConfig());
    }

    /**
     * Reads settings
     *
     * @param pConfig the config
     */
    private void readSettings(final FileConfiguration config) {
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
        readSettings(plugin.getConfig());
    }
}
