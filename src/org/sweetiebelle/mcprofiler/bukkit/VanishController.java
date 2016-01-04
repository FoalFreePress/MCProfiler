package org.sweetiebelle.mcprofiler.bukkit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.sweetiebelle.mcprofiler.Data;

import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * If this class is activated, it means VanishNoPacket was found.
 *
 */
class VanishController implements Listener {
    private final Data d;
    private final VanishPlugin vnp;

    VanishController(final Data d) {
        this.d = d;
        vnp = JavaPlugin.getPlugin(VanishPlugin.class);
    }

    /**
     * Updates player information from a VanishEvent
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onVanish(final VanishStatusChangeEvent pEvent) {
        final Player p = pEvent.getPlayer();
        d.updatePlayerInformation(p.getUniqueId(), p.getName(), p.getAddress().toString().split("/")[1].split(":")[0]);
        Location loc = p.getLocation();
        d.setPlayerLastPosition(p.getUniqueId(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * Checks if the sender can see the admin
     * @param admin the player to check if they are vanished
     * @param sender the player
     * @return true if they can see them, else false
     */
    boolean canSee(final Player admin, final Player sender) {
        if (vnp.getManager().isVanished(admin)) {
            if (PermissionsEx.getPermissionManager().has(sender, "vanish.see"))
                return true;
            return false;
        }
        return true;
    }
}
