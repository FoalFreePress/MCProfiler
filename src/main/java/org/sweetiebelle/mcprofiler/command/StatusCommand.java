package org.sweetiebelle.mcprofiler.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.IVanish;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.Note;
import org.sweetiebelle.mcprofiler.api.account.Permission;
import org.sweetiebelle.mcprofiler.api.response.OnlineResponse;
import org.sweetiebelle.mcprofiler.controller.vanish.Controllers;
import org.sweetiebelle.mcprofiler.api.response.NameResponse;

public class StatusCommand extends AbstractCommand {

    private IVanish vc;

    public StatusCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
        vc = Controllers.getVanish();
    }

    public boolean execute(CommandSender sender, String playername) {
        Permission perm = new Permission(sender);
        CompletableFuture<Optional<Account>> future = getAccount(playername, true);
        future.thenAccept((ao) -> {
            if (!ao.isPresent()) {
                sendMessage(sender, ChatColor.RED + "Could not find the player '" + ChatColor.RESET + playername + ChatColor.RED + "' in the database!");
                return;
            }
            Account a = ao.get();
            if (perm.canSeeBasicName()) {
                // Get the UUID for the given player and read the note
                // Print a "summary" of the given player
                sendMessage(sender, ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName());
                if (perm.canSeeBasicUUID())
                    sendMessage(sender, ChatColor.AQUA + "* [" + a.getUUID().toString() + "]");
                if (perm.canSeeBasicPreviousUsernames()) {
                    NameResponse[] previousUsernames = a.getPreviousNames();
                    sendMessage(sender, ChatColor.RED + "Player " + a.getName() + " has the known previous usernames:");
                    for (NameResponse response : previousUsernames) {
                        String time = getTimeStamp(response.changedToAt);
                        if (response.changedToAt == 0L)
                            time = "Original Name      ";
                        sendMessage(sender, ChatColor.AQUA + " " + time + " " + response.name);
                    }
                }
            } else {
                sendMessage(sender, SweetieLib.NO_PERMISSION);
                return;
            } // Check if the player is online or not
            OnlineResponse response = new OnlineResponse(plugin, sender, vc);
            if (sender.hasPermission("mcprofiler.info.online"))
                if (response.isOnline && response.senderCanSee)
                    sendMessage(sender, ChatColor.RED + "- " + ChatColor.RESET + "Last on: " + ChatColor.GREEN + "Online now");
                else
                    sendMessage(sender, ChatColor.RED + "- " + ChatColor.RESET + "Last on: " + ChatColor.BLUE + a.getLastOn());
            if (sender.hasPermission("mcprofiler.info.ip"))
                sendMessage(sender, ChatColor.RED + "- " + ChatColor.RESET + "Peer address: " + ChatColor.BLUE + a.getIP());
            if (sender.hasPermission("mcprofiler.readnotes")) {
                Note[] notes = a.getNotes();
                if (notes.length == 0) {
                    sendMessage(sender, ChatColor.RED + "No notes were found.");
                }
                for (Note note : notes) {
                    sendMessage(sender, note.toString());
                }
            }
            if (sender.hasPermission("mcprofiler.info.position")) {
                if (response.isOnline && response.senderCanSee && response.queriedPlayer != null)
                    sendMessage(sender, ChatColor.RED + "- " + ChatColor.RESET + "Location: " + ChatColor.BLUE + API.locationToString(response.queriedPlayer.getLocation()));
                else if (a.getLocation() != null)
                    sendMessage(sender, ChatColor.RED + "- " + ChatColor.RESET + "Location: " + ChatColor.BLUE + a.getLocation());
                else
                    sendMessage(sender, ChatColor.RED + "- " + ChatColor.RESET + "Location: " + ChatColor.BLUE + "null");
            }
        });
        return true;
    }

    private String getTimeStamp(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating
        return sdf.format(new Date(time));
    }
}
