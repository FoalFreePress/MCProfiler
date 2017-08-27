package org.sweetiebelle.mcprofiler.sponge;

import java.util.UUID;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.sweetiebelle.mcprofiler.CommandSupplement;
import org.sweetiebelle.mcprofiler.Data;
import org.sweetiebelle.mcprofiler.Settings;

/**
 * Handles events
 *
 */
public class EventManager {

    private final CommandSupplement<CommandSource> cs;
    private final Data d;
    private final Settings s;

    public EventManager(final Data d, final CommandSupplement<CommandSource> cs, final Settings s) {
        this.d = d;
        this.cs = cs;
        this.s = s;
    }

    /**
     * Sends staff possible alt information
     * 
     * @param pEvent
     */
    @Listener
    public void onPlayerJoin(final ClientConnectionEvent.Join pEvent) {
        final Player p = pEvent.getTargetEntity();
        cs.notifyStaffOfPossibleAlts(p.getUniqueId(), p.getName(), d.getAltsOfPlayer(p.getUniqueId(), s.recOnJoin));
    }

    /**
     * Store player IP and information.
     * 
     * @param pEvent
     */
    @Listener
    public void onPlayerLogin(final ClientConnectionEvent.Login pEvent) {
        final User player = pEvent.getTargetUser();
        final UUID uuid = player.getUniqueId();
        final String name = player.getName();
        final String ip = pEvent.getConnection().getAddress().getAddress().toString().replace("/", "");
        d.storePlayerIP(uuid, ip);
        d.updatePlayerInformation(uuid, name, ip);
    }

    /**
     * Store player information and last position
     * 
     * @param pEvent
     */
    @Listener
    public void onPlayerQuit(final ClientConnectionEvent.Disconnect pEvent) {
        final Player player = pEvent.getTargetEntity();
        final Location<World> loc = player.getLocation();
        d.setPlayerLastPosition(player.getUniqueId(), loc.getExtent().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        d.updatePlayerInformation(player.getUniqueId(), player.getName(), player.getConnection().getAddress().getAddress().toString().replace("/", ""));
    }
}
