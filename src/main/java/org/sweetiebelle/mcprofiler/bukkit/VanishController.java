package org.sweetiebelle.mcprofiler.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.sweetiebelle.mcprofiler.Data;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.VanishAPI;
import net.milkbowl.vault.permission.Permission;

/**
 * Manages if players are vanished or not.
 *
 */
class VanishController implements Listener {

    private final Permission perm;
    private final boolean vnp;
    private final boolean spv;

    VanishController(final MCProfilerPlugin pl, final Data d) {
        perm = Bukkit.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
        if (Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) {
            vnp = true;
            Bukkit.getPluginManager().registerEvents(new Listener() {

                /**
                 * Updates player information from a VanishStatusChangeEvent
                 * 
                 * @param pEvent
                 */
                @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
                public void onVanish(final VanishStatusChangeEvent pEvent) {
                    final Player p = pEvent.getPlayer();
                    final Location loc = p.getLocation();
                    d.getExecutor().submit(() -> {
                        d.updatePlayerInformation(p.getUniqueId(), p.getName(), p.getAddress().toString().split("/")[1].split(":")[0]);
                        d.setPlayerLastPosition(p.getUniqueId(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                    });
                }
            }, pl);
        } else {
            vnp = false;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            spv = true;
            Bukkit.getPluginManager().registerEvents(new Listener() {

                /**
                 * Updates player information from a PlayerHideEvent
                 * 
                 * @param pEvent
                 */
                @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
                public void onVanish(final PlayerHideEvent pEvent) {
                    final Player p = pEvent.getPlayer();
                    final Location loc = p.getLocation();
                    d.getExecutor().submit(() -> {
                        d.updatePlayerInformation(p.getUniqueId(), p.getName(), p.getAddress().toString().split("/")[1].split(":")[0]);
                        d.setPlayerLastPosition(p.getUniqueId(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                    });
                }
            }, pl);
        } else {
            spv = false;
        }
    }

    /**
     * Checks if the sender can see the admin
     * 
     * @param admin
     *            the player to check if they are vanished
     * @param sender
     *            the player
     * @return true if they can see them, else false
     */
    boolean canSee(final Player admin, final Player sender) {
        if (vnp) {
            if (JavaPlugin.getPlugin(VanishPlugin.class).getManager().isVanished(admin)) {
                return perm.has(sender, "vanish.see");
            }
        }
        if (spv) {
            return VanishAPI.canSee(sender, admin);
        }
        return true;
    }
}
