package org.sweetiebelle.mcprofiler.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;

public class HelpCommand extends AbstractCommand {
    
    
    public HelpCommand(API api, LuckPermsManager manager) {
        super(api, manager);
    }

    public boolean execute(CommandSender sender) {
        if (sender.hasPermission("mcprofiler.help")) {
            sender.sendMessage(ChatColor.AQUA + "MCProfiler help menu:");
            sender.sendMessage(ChatColor.RED + "/MCProfiler addnote <playerName> <note> " + ChatColor.RESET + " - Adds a note to the given player");
            sender.sendMessage(ChatColor.RED + "/MCProfiler readnotes <playerName> " + ChatColor.RESET + " - Displays the notes on the given player");
            sender.sendMessage(ChatColor.RED + "/MCProfiler info <playerName|uuid> " + ChatColor.RESET + " - Displays a summary of the player");
            sender.sendMessage(ChatColor.RED + "/status <playername|uuid> " + ChatColor.RESET + " - short for /MCProfiler info");
            sender.sendMessage(ChatColor.RED + "/MCProfiler lookup <ip> " + ChatColor.RESET + " - Displays all accounts linked to the given IP");
            sender.sendMessage(ChatColor.RED + "/MCProfiler listlinks [-r] <playerName> " + ChatColor.RESET + " - Displays all accounts that might be linked to the given user. Use the -r flag for recursive player searching. This displays all of the alts of the player's alts, and all the alts of those alts....");
            sender.sendMessage(ChatColor.RED + "/MCProfiler listips <playerName> " + ChatColor.RESET + " - Lists all known IPs from a given player");
            sender.sendMessage(ChatColor.RED + "/MCProfiler uuid <uuid> " + ChatColor.RESET + " - Displays a username based on a UUID.");
            sender.sendMessage(ChatColor.RED + "/MCProfiler maintenance <fixnotes | forcemakeaccount | updatename> <args> " + ChatColor.RESET + " - Performs maintence commands See /MCProfiler maintenance with no args for help.");
            sender.sendMessage(ChatColor.RED + "/MCProfiler reload" + ChatColor.RESET + " - Reloads general configuration settings.");
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
