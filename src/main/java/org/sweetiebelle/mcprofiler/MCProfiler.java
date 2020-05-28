package org.sweetiebelle.mcprofiler;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.bukkit.plugin.java.JavaPlugin;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.connection.ConnectionManager;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.command.handler.CoreCommandHandler;
import org.sweetiebelle.mcprofiler.command.handler.NoteCommandHandler;
import org.sweetiebelle.mcprofiler.command.handler.StatusCommandHandler;
import org.sweetiebelle.mcprofiler.controller.DefaultLuckPermsImpl;
import org.sweetiebelle.mcprofiler.scheduler.SchedulerAdapter;

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
    private static SchedulerAdapter scheduler;

    public static <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, scheduler.async());
    }

    public static CompletableFuture<Void> makeFuture(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, scheduler.async());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        scheduler.shutdownScheduler();
        scheduler.shutdownExecutor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        s = new Settings(this);
        scheduler = new SchedulerAdapter(this);
        try {
            lpManager = SweetieLib.getPlugin().getPermission();
        } catch (Exception e) {
            getLogger().warning("Couldn't find a permission plugin! Using a default implementation.");
            lpManager = new DefaultLuckPermsImpl();
        }
        try {
            connectionManager = SweetieLib.getPlugin().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
        d = new Data(this, connectionManager, s);
        api = new API(d);
        em = new EventManager(this, lpManager, api, s);
        getServer().getPluginManager().registerEvents(em, this);
        getCommand("MCProfiler").setExecutor(new CoreCommandHandler(this, s, api, lpManager));
        getCommand("status").setExecutor(new StatusCommandHandler(this, s, api, lpManager));
        getCommand("note").setExecutor(new NoteCommandHandler(this, api, lpManager));
    }
}
