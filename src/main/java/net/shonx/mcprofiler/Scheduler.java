package net.shonx.mcprofiler;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.bukkit.plugin.Plugin;
import net.shonx.lib.scheduler.SchedulerAdapter;

public class Scheduler {

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

    private static SchedulerAdapter scheduler;
    public Scheduler(Plugin plugin) {
        scheduler = new SchedulerAdapter(plugin);
    }

    public void shutdown() {
        scheduler.shutdownScheduler();
        scheduler.shutdownExecutor();
    }
}
