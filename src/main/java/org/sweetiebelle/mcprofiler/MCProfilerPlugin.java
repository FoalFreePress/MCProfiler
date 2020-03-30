package org.sweetiebelle.mcprofiler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class.
 */
public class MCProfilerPlugin extends JavaPlugin {

    private CommandHandler ch;
    private Settings s;
    private EventManager em;
    private Data d;

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
        s = new Settings(this);
        d = new Data(this, s);
        ch = new CommandHandler(s, d, this);
        em = new EventManager(d, ch, s);
        getServer().getPluginManager().registerEvents(em, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return ch.onCommand(sender, command, label, args);
    }
}
