package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.alternate.UUIDAlt;

public class ShowAltsCommand extends AbstractCommand {

    public ShowAltsCommand(API api, LuckPermsManager manager) {
        super(api, manager);
    }

    public boolean execute(CommandSender sender, String targetName, boolean recursive) {
        if (sender.hasPermission("mcprofiler.listlinks")) {
            Optional<Account> ao = getAccount(targetName, false);
            if (ao.isPresent()) {
                Account a = ao.get();
                UUIDAlt[] altAccounts = api.getAccounts(a.getUUID(), recursive);
                sender.sendMessage(ChatColor.RED + "The player " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName() + " " + ChatColor.RED + "(" + ChatColor.RESET + a.getIP() + ChatColor.RED + ") has the following associated accounts:");
                for (UUIDAlt alt : altAccounts) {
                    Account fullAlt = api.getAccount(alt.getUUID()).get();
                    sender.sendMessage(ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(alt.getUUID()) + fullAlt.getName() + " " + ChatColor.RED + "(" + ChatColor.RESET + alt.getIP() + ChatColor.RED + ")");
                }
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Couldn't find the player " + ChatColor.RESET + targetName + ChatColor.RED + " in the database!");
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
