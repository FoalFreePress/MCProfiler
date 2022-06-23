package net.shonx.mcprofiler;

import org.bukkit.plugin.java.JavaPlugin;
import net.shonx.lib.SweetieLib;
import net.shonx.lib.connection.ConnectionManager;
import net.shonx.lib.permission.PermissioNotFound;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.command.handler.CoreCommandHandler;
import net.shonx.mcprofiler.command.handler.NoteCommandHandler;
import net.shonx.mcprofiler.command.handler.StatusCommandHandler;
import net.shonx.mcprofiler.controller.vanish.Controllers;

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
