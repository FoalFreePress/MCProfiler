package org.sweetiebelle.mcprofiler.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.account.Permission;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
    }

    public boolean execute(CommandSender sender) {
        Permission perm = new Permission(sender);
        sender.sendMessage(ChatColor.RED + "MCProfiler v" + MCProfiler.VERSION);
        if (perm.canAddNote())
            sender.sendMessage(ChatColor.RED + "/MCProfiler addnote <playerName> <note> " + ChatColor.RESET + " - Adds a note to the given player");
        if (perm.canReadNotes())
            sender.sendMessage(ChatColor.RED + "/MCProfiler readnotes <playerName> " + ChatColor.RESET + " - Displays the notes on the given player");
        if (perm.canSeeBasicName()) {
            sender.sendMessage(ChatColor.RED + "/MCProfiler info <playerName|uuid> " + ChatColor.RESET + " - Displays a summary of the player");
            sender.sendMessage(ChatColor.RED + "/status <playername|uuid> " + ChatColor.RESET + " - short for /MCProfiler info");
        }
        if (perm.canLookupIP())
            sender.sendMessage(ChatColor.RED + "/MCProfiler lookup <ip> " + ChatColor.RESET + " - Displays all accounts linked to the given IP");
        if (perm.canLookupAlts())
            sender.sendMessage(ChatColor.RED + "/MCProfiler listlinks [-r] <playerName> " + ChatColor.RESET + " - Displays all accounts that might be linked to the given user. Use the -r flag for recursive player searching. This displays all of the alts of the player's alts, and all the alts of those alts....");
        if (perm.canListIPs())
            sender.sendMessage(ChatColor.RED + "/MCProfiler listips <playerName> " + ChatColor.RESET + " - Lists all known IPs from a given player");
        if (perm.canUseUUID())
            sender.sendMessage(ChatColor.RED + "/MCProfiler uuid <uuid> " + ChatColor.RESET + " - Displays a username based on a UUID.");
        if (perm.canReload())
            sender.sendMessage(ChatColor.RED + "/MCProfiler reload" + ChatColor.RESET + " - Reloads general configuration settings.");
        return true;
    }
}
