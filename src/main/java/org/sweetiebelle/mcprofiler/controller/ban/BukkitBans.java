package org.sweetiebelle.mcprofiler.controller.ban;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.sweetiebelle.mcprofiler.Scheduler;
import org.sweetiebelle.mcprofiler.api.IBans;

public class BukkitBans implements IBans {

    @Override
    public CompletableFuture<boolean[]> isBanned(UUID[] players) {
        return Scheduler.makeFuture(() -> {
            boolean[] results = new boolean[players.length];

            for (int i = 0; i < players.length; i++) {
                results[i] = Bukkit.getOfflinePlayer(players[i]).isBanned();
            }
            return results;
        });
    }

    @Override
    public CompletableFuture<Boolean> isBanned(UUID player) {
        return Scheduler.makeFuture(() -> {
            return Bukkit.getOfflinePlayer(player).isBanned();
        });
    }

    @Override
    public boolean isBannedDangerous(UUID player) {
        return Bukkit.getOfflinePlayer(player).isBanned();
    }

}
