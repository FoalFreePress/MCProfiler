package org.sweetiebelle.mcprofiler;

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

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        i = this;
        s = new Settings(this);
        d = new Data(this, s);
        cs = new CommandSupplement(s, d, this);
        ch = new CommandHandler(cs);
        getCommand("MCProfiler").setExecutor(ch);
        getServer().getPluginManager().registerEvents(new EventManager(d, cs), this);
    }

    /**
     * Debug message. Static to allow classes that usually wouldn't have an isntance of the plugin to access it.
     * @param message
     */
    static void debug(final String message) {
        if (i.s.useDebug)
            i.getLogger().info("[DEBUG] " + message);
    }
}
