package org.sweetiebelle.mcprofiler.sponge;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.sweetiebelle.mcprofiler.Data;
import org.sweetiebelle.mcprofiler.Settings;

import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "mcprofiler", name = "MCProfiler", version = "1.5", description = "Allows the creation of profiles on users. Tracks their IP, and allows creation of notes.")
public class MCProfilerPlugin {

    public final static Logger logger = LogManager.getLogger();
    private Settings s;
    @SuppressWarnings("unused")
    private CommandHandler ch;
    private EventManager em;
    private Data d;
    private CommandSupplementSponge cs;
    @Inject
    @DefaultConfig(sharedRoot = true)
    Path defaultConfig;
    @Inject
    @DefaultConfig(sharedRoot = true)
    ConfigurationLoader<CommentedConfigurationNode> configManager;
    @Inject
    @ConfigDir(sharedRoot = false)
    Path privateConfigDir;
    CommentedConfigurationNode config;

    @Listener
    public void onEnable(final GameStartedServerEvent event) {
        config = HoconConfigurationLoader.builder().setPath(defaultConfig).build().createEmptyNode(ConfigurationOptions.defaults());
        s = new SpongeSettings(this);
        d = new Data(s);
        cs = new CommandSupplementSponge(s, d, this);
        ch = new CommandHandler(this, cs);
        em = new EventManager(d, cs, s);
        Sponge.getGame().getEventManager().registerListeners(this, em);
    }

    public CommentedConfigurationNode getDefaultNode() {
        return config;
    }

    public Path getPrivatePath() {
        return privateConfigDir;
    }

    public Path getConfigPath() {
        return defaultConfig;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
        return configManager;
    }
}