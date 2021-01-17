package org.sweetiebelle.mcprofiler.controller.ban;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.plugin.java.JavaPlugin;
import org.sweetiebelle.mcprofiler.Scheduler;
import org.sweetiebelle.mcprofiler.api.IBans;
import org.sweetiebelle.mcprofiler.api.exception.BanPluginNotLoadedException;

import com.brohoof.brohoofbans.BrohoofBansPlugin;

public class BrohoofBans implements IBans {
    private final BrohoofBansPlugin brohoofBans;

    public BrohoofBans() throws BanPluginNotLoadedException {
        try {
            brohoofBans = JavaPlugin.getPlugin(BrohoofBansPlugin.class);
        } catch (Throwable e) {
            throw new BanPluginNotLoadedException(e);
        }
    }

    @Override
    public CompletableFuture<boolean[]> isBanned(UUID[] players) {
        return Scheduler.makeFuture(() -> {
            boolean[] results = new boolean[players.length];

            for (int i = 0; i < players.length; i++) {
                results[i] = brohoofBans.getAPI().isBanned(players[i]).get();
            }
            return results;
        });
    }

    @Override
    public CompletableFuture<Boolean> isBanned(UUID player) {
        return Scheduler.makeFuture(() -> {
            return brohoofBans.getAPI().isBanned(player).get();
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBannedDangerous(UUID player) {
        return brohoofBans.getAPI().isBannedDangerous(player);
    }

}
