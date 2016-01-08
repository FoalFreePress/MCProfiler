package org.sweetiebelle.mcprofiler.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.sweetiebelle.mcprofiler.Data;
import org.sweetiebelle.mcprofiler.Settings;

/**
 * The main plugin class.
 */
public class MCProfilerPlugin extends JavaPlugin {
    private CommandHandler ch;
    private Settings s;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        s = new BukkitSettings(this);
        final Data d = new Data(s);
        final CommandSupplementBukkit cs = new CommandSupplementBukkit(s, d, this);
        ch = new CommandHandler(cs);
        getServer().getPluginManager().registerEvents(new EventManager(d, cs, s), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return ch.onCommand(sender, command, label, args);
    }
}
