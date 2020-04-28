package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.api.account.Account;

public class UUIDLookupCommand extends AbstractCommand {

    public UUIDLookupCommand(API api, LuckPermsManager manager) {
        super(api, manager);
    }

    public boolean execute(CommandSender pSender, String playerUUID) {
        if (pSender.hasPermission("mcprofiler.uuid")) {
            Optional<Account> ao = null;
            Account a = null;
            try {
                ao = api.getAccount(UUID.fromString(playerUUID));
            } catch (IllegalArgumentException e) {
                pSender.sendMessage(ChatColor.RED + "That is not a UUID.");
                return true;
            }
            if (ao.isPresent()) {
                a = ao.get();
                pSender.sendMessage(ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName());
                pSender.sendMessage(ChatColor.AQUA + "* [" + a.getUUID().toString() + "]");
                return true;
            }
            pSender.sendMessage(ChatColor.RED + "No player with UUID " + ChatColor.RESET + playerUUID + ChatColor.RED + " could be found.");
            return true;
        }
        pSender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
