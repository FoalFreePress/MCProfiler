package org.sweetiebelle.mcprofiler.controller.vanish;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.IVanish;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.exception.VanishPluginNotLoadedException;

public class VNPVanish implements IVanish {

    private final API api;

    public VNPVanish(MCProfiler plugin, API api) throws VanishPluginNotLoadedException {
        if(!Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) {
            plugin.getLogger().info("I couldn't find VanishNoPacket!");
            throw new VanishPluginNotLoadedException();
        }
        this.api = api;
        Bukkit.getPluginManager().registerEvents(new VanishListener(), plugin);
    }

    @Override
    public boolean canSee(Player admin, Player sender) {
        return sender.hasPermission("vanish.see");
    }

    public class VanishListener implements Listener {
        /**
         * Updates player information from a VanishStatusChangeEvent
         *
         * @param pEvent
         */
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onVanish(VanishStatusChangeEvent pEvent) {
            Player player = pEvent.getPlayer();
            UUID uuid = player.getUniqueId();
            String name = player.getName();
            String ip = player.getAddress().getAddress().toString().split("/")[1];
            CompletableFuture<Optional<Account>> future = api.getAccount(uuid);
            future.thenAccept((oAccount) -> {
                if (oAccount.isPresent())
                    api.savePlayerInformation(new Account(uuid, name, oAccount.get().getLastOn(), API.locationToString(player.getLocation()), ip, oAccount.get().getNotes(), oAccount.get().getPreviousNames()));
                else
                    api.createPlayerInformation(new Account(uuid, name, null, API.locationToString(player.getLocation()), ip, null, null));
            });
        }
    }
}
