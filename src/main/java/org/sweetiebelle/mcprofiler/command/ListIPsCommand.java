package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.api.account.Account;

public class ListIPsCommand extends AbstractCommand {

    public ListIPsCommand(API api, LuckPermsManager manager) {
        super(api, manager);
    }

    public boolean execute(CommandSender sender, String playerName) {
        Optional<Account> ao = getAccount(playerName, false);
        if (!ao.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Could not find the player " + ChatColor.RESET + playerName + ChatColor.RED + " in the database!");
            return true;
        }
        Account a = ao.get();
        if (sender.hasPermission("mcprofiler.listips")) {
            String[] ips = api.getIPs(a);
            sender.sendMessage(ChatColor.RED + "The ips linked to the player " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName() + ChatColor.RESET + ChatColor.RED + " are:");
            for (String ip : ips)
                sender.sendMessage(ChatColor.AQUA + "* " + ip);
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
