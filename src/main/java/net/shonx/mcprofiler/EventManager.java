package net.shonx.mcprofiler;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.api.account.Account;
import net.shonx.mcprofiler.api.account.Note;
import net.shonx.mcprofiler.command.NotifyStaffCommand;

/**
 * Handles events
 *
 */
public class EventManager implements Listener {

    private final API api;
    private final NotifyStaffCommand notifyStaff;
    private final Settings s;
    private final MCProfiler plugin;

    public EventManager(MCProfiler plugin, PermissionManager chat, API api, Settings s) {
        this.plugin = plugin;
        this.api = api;
        this.s = s;
        notifyStaff = new NotifyStaffCommand(plugin, s, api, chat);
    }

    /**
     * Sends staff possible alt information
     *
     * @param pEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent pEvent) {
        Player p = pEvent.getPlayer();
        notifyStaff.execute(api.getAccount(pEvent.getPlayer().getUniqueId()), api.getAccounts(p.getUniqueId(), s.recOnJoin));
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
        CompletableFuture<Optional<Account>> future = api.getAccount(uuid);
        try {
            future.thenAccept((oAccount) -> {
                try {
                    if (oAccount.isPresent()) {
                        api.savePlayerInformation(new Account(uuid, name, oAccount.get().getLastOn(), API.locationToString(player.getLocation()), ip, oAccount.get().getNotes(), oAccount.get().getPreviousNames())).thenAccept((object) -> {
                            if (s.additionalMessages)
                                plugin.getLogger().info("Updated player information for " + name);
                        }).get();
                    } else {
                        api.createPlayerInformation(new Account(uuid, name, null, API.locationToString(player.getLocation()), ip, new Note[0], null)).thenAccept((object) -> {
                            if (s.additionalMessages)
                                plugin.getLogger().info("Created player information for " + name);
                        }).get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        CompletableFuture<Optional<Account>> future = api.getAccount(player.getUniqueId());
        future.thenAccept((oAccount) -> {
            if (oAccount.isPresent())
                api.savePlayerInformation(new Account(uuid, name, oAccount.get().getLastOn(), API.locationToString(player.getLocation()), ip, oAccount.get().getNotes(), oAccount.get().getPreviousNames())).thenAccept((object) -> {
                    if (s.additionalMessages)
                        plugin.getLogger().info("Updated player information for " + name);
                });
            else
                api.createPlayerInformation(new Account(uuid, name, null, API.locationToString(player.getLocation()), ip, new Note[0], null)).thenAccept((object) -> {
                    if (s.additionalMessages)
                        plugin.getLogger().info("Created player information for " + name);
                });
        });
    }
}
