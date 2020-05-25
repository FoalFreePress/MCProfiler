package org.sweetiebelle.mcprofiler;

import org.bukkit.plugin.java.JavaPlugin;
import org.sweetiebelle.lib.ConnectionManager;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.command.handlers.CoreCommandHandler;
import org.sweetiebelle.mcprofiler.command.handlers.NoteCommandHandler;
import org.sweetiebelle.mcprofiler.command.handlers.StatusCommandHandler;

/**
 * The main plugin class.
 */
public class MCProfiler extends JavaPlugin {

    public static final String VERSION = "2.0";
    private API api;
    private ConnectionManager connectionManager;
    private Data d;
    private EventManager em;
    private LuckPermsManager lpManager;
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
        s = new Settings(this);
        try {
            lpManager = SweetieLib.getLuckPerms();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            connectionManager = SweetieLib.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        d = new Data(this, connectionManager, s);
        api = new API(d);
        em = new EventManager(lpManager, api, s);
        getServer().getPluginManager().registerEvents(em, this);
        getCommand("MCProfiler").setExecutor(new CoreCommandHandler(this, s, api, lpManager));
        getCommand("status").setExecutor(new StatusCommandHandler(this, api, lpManager));
        getCommand("note").setExecutor(new NoteCommandHandler(api, lpManager));
    }
}
