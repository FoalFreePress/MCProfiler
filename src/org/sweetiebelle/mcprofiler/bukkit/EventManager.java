package org.sweetiebelle.mcprofiler.bukkit;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.sweetiebelle.mcprofiler.CommandSupplement;
import org.sweetiebelle.mcprofiler.Data;
import org.sweetiebelle.mcprofiler.Settings;

/**
 * Handles events
 *
 */
class EventManager implements Listener {
    private final CommandSupplement<CommandSender> cs;
    private final Data d;
    private final Settings s;

    public EventManager(final Data d, final CommandSupplement<CommandSender> cs, final Settings s) {
        this.d = d;
        this.cs = cs;
        this.s = s;
    }

    /**
     * Sends staff possible alt information
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent pEvent) {
        final Player p = pEvent.getPlayer();
        cs.notifyStaffOfPossibleAlts(p.getUniqueId(), p.getName(), d.getAltsOfPlayer(p.getUniqueId(), s.recOnJoin));
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
        final String ip = pEvent.getAddress().toString().split("/")[1];
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
        final Location loc = player.getLocation();
        d.setPlayerLastPosition(player.getUniqueId(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        d.updatePlayerInformation(player.getUniqueId(), player.getName(), player.getAddress().getAddress().toString().split("/")[1]);
    }
}
