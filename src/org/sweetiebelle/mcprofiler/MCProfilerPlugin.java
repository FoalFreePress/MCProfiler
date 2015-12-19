package org.sweetiebelle.mcprofiler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class.
 */
public class MCProfilerPlugin extends JavaPlugin {
    private CommandHandler ch;
    private CommandSupplement cs;
    private Data d;
    private Settings s;
    static MCProfilerPlugin i;

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
        i = this;
        s = new Settings(this);
        d = new Data(this, s);
        cs = new CommandSupplement(s, d, this);
        ch = new CommandHandler(cs);
        getServer().getPluginManager().registerEvents(new EventManager(d, cs), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return ch.onCommand(sender, command, label, args);
    }

    /**
     * Debug message. Static to allow classes that usually wouldn't have an instance of the plugin to access it.
     * @param message
     */
    static void debug(final String message) {
        if (i.s.useDebug)
            i.getLogger().info("[DEBUG] " + message);
    }
}
