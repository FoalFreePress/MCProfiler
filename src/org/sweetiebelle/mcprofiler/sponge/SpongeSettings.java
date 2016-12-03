package org.sweetiebelle.mcprofiler.sponge;

import java.io.File;
import java.io.IOException;

import org.sweetiebelle.mcprofiler.Settings;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class SpongeSettings extends Settings {
    private final MCProfilerPlugin p;

    public SpongeSettings(final MCProfilerPlugin p) {
        this.p = p;
        checkExists();
        readSettings();
    }

    private void checkExists() {
        try {
            if (!p.getConfigPath().toFile().exists()) {
                final File file = p.getConfigPath().toFile();
                file.createNewFile();
                final CommentedConfigurationNode config = p.getConfigManager().load();
                config.getNode("database", "database", "name").setValue("database");
                config.getNode("database", "host", "name").setValue("localhost");
                config.getNode("database", "password", "name").setValue("password");
                config.getNode("database", "port", "name").setValue("3306");
                config.getNode("database", "prefix", "name").setValue("MCProfiler_");
                config.getNode("database", "username", "name").setValue("user");
                config.getNode("database", "printStackTraces", "enabled").setValue(false);
                config.getNode("database", "showquery", "enabled").setValue(false);
                config.getNode("database", "debug", "enabled").setValue(false);
                config.getNode("database", "recursivePlayerJoin", "enabled").setValue(false);
                p.getConfigManager().save(config);
            }
            p.config = p.getConfigManager().load();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void readSettings() {
        dbDatabase = p.config.getNode("database", "database", "name").getString();
        dbHost = p.config.getNode("database", "host", "name").getString();
        dbPass = p.config.getNode("database", "password", "name").getString();
        dbPort = p.config.getNode("database", "port", "name").getString();
        dbPrefix = p.config.getNode("database", "prefix", "name").getString();
        dbUser = p.config.getNode("database", "username", "name").getString();
        stackTraces = p.config.getNode("general", "printStackTraces", "enabled").getBoolean();
        showQuery = p.config.getNode("general", "showquery", "enabled").getBoolean();
        useDebug = p.config.getNode("general", "debug", "enabled").getBoolean();
        recOnJoin = p.config.getNode("general", "recursivePlayerJoin", "enabled").getBoolean();
    }

    @Override
    public void reloadSettings() {
        readSettings();
    }
}
