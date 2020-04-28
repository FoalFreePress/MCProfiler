package org.sweetiebelle.mcprofiler;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.command.NotifyStaffCommand;

/**
 * Handles events
 *
 */
public class EventManager implements Listener {

    private API api;
    private NotifyStaffCommand notifyStaff;
    private Settings s;

    public EventManager(LuckPermsManager chat, API api, Settings s) {
        this.api = api;
        this.s = s;
        notifyStaff = new NotifyStaffCommand(api, chat);
    }

    /**
     * Sends staff possible alt information
     *
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent pEvent) {
        Player p = pEvent.getPlayer();
        notifyStaff.execute(api.getAccount(pEvent.getPlayer().getUniqueId()).get(), api.getAccounts(p.getUniqueId(), s.recOnJoin));
    }

    /**
     * Store player IP and information.
     *
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerLogin(PlayerLoginEvent pEvent) {
        Player player = pEvent.getPlayer();
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        String ip = pEvent.getAddress().toString().split("/")[1];
        Optional<Account> oAccount = api.getAccount(uuid);
        if (oAccount.isPresent())
            api.updatePlayerInformation(new Account(uuid, name, oAccount.get().getLastOn(), API.locationToString(player.getLocation()), ip, oAccount.get().getNotes(), oAccount.get().getPreviousNames(), true));
        else
            api.updatePlayerInformation(new Account(uuid, name, null, API.locationToString(player.getLocation()), ip, null, null, false));
    }

    /**
     * Store player information and last position
     *
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent pEvent) {
        Player player = pEvent.getPlayer();
        UUID uuid = player.getUniqueId();
        String ip = player.getAddress().getAddress().toString().split("/")[1];
        String name = player.getName();
        Optional<Account> oAccount = api.getAccount(player.getUniqueId());
        if (oAccount.isPresent())
            api.updatePlayerInformation(new Account(uuid, name, oAccount.get().getLastOn(), API.locationToString(player.getLocation()), ip, oAccount.get().getNotes(), oAccount.get().getPreviousNames(), true));
        else
            api.updatePlayerInformation(new Account(uuid, name, null, API.locationToString(player.getLocation()), ip, null, null, false));
    }
}
