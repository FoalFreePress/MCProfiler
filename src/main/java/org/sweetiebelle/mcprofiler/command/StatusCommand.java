package org.sweetiebelle.mcprofiler.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.NamesFetcher.Response;
import org.sweetiebelle.mcprofiler.VanishController;
import org.sweetiebelle.mcprofiler.api.account.Account;

public class StatusCommand extends AbstractCommand {

    private VanishController vc;

    public StatusCommand(MCProfiler plugin, API api, LuckPermsManager manager) {
        super(api, manager);
        vc = new VanishController(plugin, api);
    }

    public boolean execute(CommandSender pSender, String playername) {
        Optional<Account> ao = getAccount(playername, true);
        if (!ao.isPresent()) {
            pSender.sendMessage(ChatColor.RED + "Could not find the player '" + ChatColor.RESET + playername + ChatColor.RED + "' in the database!");
            return true;
        }
        Account a = ao.get();
        if (pSender.hasPermission("mcprofiler.info.basic.name")) {
            // Get the UUID for the given player and read the note
            // Print a "summary" of the given player
            pSender.sendMessage(ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName());
            if (pSender.hasPermission("mcprofiler.info.basic.uuid"))
                pSender.sendMessage(ChatColor.AQUA + "* [" + a.getUUID().toString() + "]");
            if (pSender.hasPermission("mcprofiler.info.basic.previoususernames")) {
                Response[] previousUsernames = a.getPreviousNames();
                pSender.sendMessage(ChatColor.RED + "Player " + a.getName() + " has the known previous usernames:");
                for (Response response : previousUsernames) {
                    String time = getTimeStamp(response.changedToAt);
                    if (response.changedToAt == 0L)
                        time = "Original Name      ";
                    pSender.sendMessage(ChatColor.AQUA + " " + time + " " + response.name);
                }
            }
        } else {
            pSender.sendMessage(SweetieLib.NO_PERMISSION);
            return true;
        }
        // Check if the player is online or not
        boolean isOnline = false;
        boolean senderCanSee = true;
        Player queriedPlayer = null;
        if (pSender instanceof Player) {
            senderCanSee = false;
            for (Player player : Bukkit.getServer().getOnlinePlayers())
                if (player.getName().equals(a.getName())) {
                    isOnline = true;
                    senderCanSee = vc.canSee(player, (Player) pSender);
                    queriedPlayer = player;
                    break;
                }
        } else {
            senderCanSee = true;
            for (Player player : Bukkit.getServer().getOnlinePlayers())
                if (player.getName().equals(a.getName())) {
                    isOnline = true;
                    queriedPlayer = player;
                    break;
                }
        }
        if (pSender.hasPermission("mcprofiler.info.online"))
            if (isOnline && senderCanSee)
                pSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + "Last on: " + ChatColor.GREEN + "Online now");
            else
                pSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + "Last on: " + ChatColor.BLUE + a.getLastOn());
        if (pSender.hasPermission("mcprofiler.info.ip"))
            pSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + "Peer address: " + ChatColor.BLUE + a.getIP());
        if (pSender.hasPermission("mcprofiler.readnotes"))
            pSender.sendMessage(a.getNotes());
        if (pSender.hasPermission("mcprofiler.info.position"))
            if (isOnline && senderCanSee && queriedPlayer != null)
                pSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + "Location: " + ChatColor.BLUE + API.locationToString(queriedPlayer.getLocation()));
            else if (a.getLocation() != null)
                pSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + "Location: " + ChatColor.BLUE + a.getLocation());
            else
                pSender.sendMessage(ChatColor.RED + "- " + ChatColor.RESET + "Location: " + ChatColor.BLUE + "null");
        return true;
    }

    private String getTimeStamp(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating
        return sdf.format(new Date(time));
    }
}
