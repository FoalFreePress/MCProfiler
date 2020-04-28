package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.api.account.Account;

public class ReadNotesCommand extends AbstractCommand {

    public ReadNotesCommand(API api, LuckPermsManager manager) {
        super(api, manager);
    }

    public boolean execute(CommandSender sender, String playerName) {
        if (sender.hasPermission("mcprofiler.readnotes")) {
            Optional<Account> ao = getAccount(playerName, false);
            if (ao.isPresent())
                sender.sendMessage(ao.get().getNotes());
            else
                sender.sendMessage(ChatColor.RED + "Couldn't find the player " + ChatColor.RESET + playerName + ChatColor.RED + " in the database!");
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
