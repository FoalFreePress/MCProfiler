package org.sweetiebelle.mcprofiler;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.event.VanishStatusChangeEvent;
import org.sweetiebelle.mcprofiler.api.account.Account;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.VanishAPI;

/**
 * Manages if players are vanished or not.
 *
 */
public class VanishController implements Listener {

    private boolean spv;
    private boolean vnp;

    public VanishController(MCProfiler pl, API api) {
        if (Bukkit.getPluginManager().isPluginEnabled("VanishNoPacket")) {
            vnp = true;
            Bukkit.getPluginManager().registerEvents(new Listener() {

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
                    Optional<Account> oAccount = api.getAccount(uuid);
                    if (oAccount.isPresent())
                        api.updatePlayerInformation(new Account(uuid, name, oAccount.get().getLastOn(), API.locationToString(player.getLocation()), ip, oAccount.get().getNotes(), oAccount.get().getPreviousNames(), true));
                    else
                        api.updatePlayerInformation(new Account(uuid, name, null, API.locationToString(player.getLocation()), ip, null, null, false));
                }
            }, pl);
        } else
            vnp = false;
        if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
            spv = true;
            Bukkit.getPluginManager().registerEvents(new Listener() {

                /**
                 * Updates player information from a PlayerHideEvent
                 *
                 * @param pEvent
                 */
                @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
                public void onVanish(PlayerHideEvent pEvent) {
                    Player player = pEvent.getPlayer();
                    UUID uuid = player.getUniqueId();
                    String name = player.getName();
                    String ip = player.getAddress().getAddress().toString().split("/")[1];
                    Optional<Account> oAccount = api.getAccount(uuid);
                    if (oAccount.isPresent())
                        api.updatePlayerInformation(new Account(uuid, name, oAccount.get().getLastOn(), API.locationToString(player.getLocation()), ip, oAccount.get().getNotes(), oAccount.get().getPreviousNames(), true));
                    else
                        api.updatePlayerInformation(new Account(uuid, name, null, API.locationToString(player.getLocation()), ip, null, null, false));
                }
            }, pl);
        } else
            spv = false;
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
    public boolean canSee(Player admin, Player sender) {
        if (vnp)
            if (JavaPlugin.getPlugin(VanishPlugin.class).getManager().isVanished(admin))
                return sender.hasPermission("vanish.see");
        if (spv)
            return VanishAPI.canSee(sender, admin);
        return true;
    }
}
