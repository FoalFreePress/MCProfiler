package org.sweetiebelle.mcprofiler;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles events
 *
 */
class EventManager implements Listener {
    private final CommandSupplement cs;
    private final Data d;

    EventManager(final Data d, final CommandSupplement cs) {
        this.d = d;
        this.cs = cs;
    }

    /**
     * Sends staff possible alt information
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent pEvent) {
        final Player p = pEvent.getPlayer();
        cs.notifyStaffOfPossibleAlts(p, d.getAltsOfPlayer(p.getUniqueId(), true));
    }

    /**
     * Store player IP and information.
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerLogin(final PlayerLoginEvent pEvent) {
        final Player player = pEvent.getPlayer();
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();
        final String ip = pEvent.getAddress().toString().replace("/", "");
        d.storePlayerIP(uuid, ip);
        d.updatePlayerInformation(uuid, name, ip);
    }

    /**
     * Store player information and last position
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent pEvent) {
        final Player player = pEvent.getPlayer();
        d.setPlayerLastPosition(player.getUniqueId(), player.getLocation());
        d.updatePlayerInformation(player.getUniqueId(), player.getName(), player.getAddress().toString().split("/")[1].split(":")[0]);
    }
}
