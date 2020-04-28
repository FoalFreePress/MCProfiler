package org.sweetiebelle.mcprofiler.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.api.account.Account;

public class LookupIPCommand extends AbstractCommand {

    public LookupIPCommand(API api, LuckPermsManager manager) {
        super(api, manager);
    }

    public boolean execute(CommandSender sender, String ip) {
        if (sender.hasPermission("mcprofiler.lookup")) {
            // Find the uuids linked to the ip
            Account[] accounts = api.getAccounts(ip);
            if (accounts.length == 0) {
                sender.sendMessage(ChatColor.RED + "Didn't find any accounts linked to the ip " + ChatColor.RESET + ip + ChatColor.RED + "!");
                return true;
            }
            // Get all the uuids and iterate over them
            sender.sendMessage(ChatColor.RED + "The player accounts linked to the ip " + ChatColor.RESET + ip + ChatColor.RED + " are:");
            for (Account a : accounts) {
                sender.sendMessage(ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName());
                sender.sendMessage(ChatColor.AQUA + "* [" + a.getUUID() + "]");
            }
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
