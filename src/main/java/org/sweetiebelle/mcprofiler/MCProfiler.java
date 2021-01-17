package org.sweetiebelle.mcprofiler;

import org.bukkit.plugin.java.JavaPlugin;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.connection.ConnectionManager;
import org.sweetiebelle.lib.permission.PermissioNotFound;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.command.handler.CoreCommandHandler;
import org.sweetiebelle.mcprofiler.command.handler.NoteCommandHandler;
import org.sweetiebelle.mcprofiler.command.handler.StatusCommandHandler;
import org.sweetiebelle.mcprofiler.controller.vanish.Controllers;

/**
 * The main plugin class.
 */
public class MCProfiler extends JavaPlugin {

    public static final String VERSION = "2.2";
    private API api;
    private ConnectionManager connectionManager;
    private Data d;
    private EventManager em;
    private PermissionManager lpManager;
    private Settings s;
    private Scheduler scheduler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        scheduler.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        s = new Settings(this);
        scheduler = new Scheduler(this);
        try {
            lpManager = SweetieLib.getPlugin().getPermission();
        } catch (Exception e) {
            getLogger().warning("Couldn't find a permission plugin! Using a default implementation.");
            lpManager = new PermissioNotFound();
        }
        try {
            connectionManager = SweetieLib.getPlugin().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        d = new Data(this, connectionManager, s);
        api = new API(d);
        Controllers.getController(this, api);
        em = new EventManager(this, lpManager, api, s);
        getServer().getPluginManager().registerEvents(em, this);
        getCommand("MCProfiler").setExecutor(new CoreCommandHandler(this, s, api, lpManager));
        getCommand("status").setExecutor(new StatusCommandHandler(this, api, lpManager));
        getCommand("note").setExecutor(new NoteCommandHandler(this, api, lpManager));
    }
}
