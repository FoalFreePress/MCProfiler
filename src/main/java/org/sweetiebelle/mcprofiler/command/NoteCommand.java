package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.ConsoleAccount;

public class NoteCommand extends AbstractCommand {

    public NoteCommand(API api, LuckPermsManager manager) {
        super(api, manager);
    }

    public boolean execute(CommandSender sender, String targetPlayer, String note) {
        if (sender instanceof Player) {
            if (sender.hasPermission("mcprofiler.addnote")) {
                Optional<Account> oAccount = getAccount(targetPlayer, false);
                if (!oAccount.isPresent()) {
                    sender.sendMessage(ChatColor.RED + "Could not find the player " + ChatColor.RESET + targetPlayer + ChatColor.RED + " in the database!");
                    return true;
                }
                api.addNote(api.getAccount(((Player) sender).getUniqueId()).get(), oAccount.get(), note);
                sender.sendMessage(ChatColor.RED + "Added the note " + ChatColor.RESET + note + ChatColor.RED + " to " + ChatColor.RESET + chat.getCompletePlayerPrefix(oAccount.get().getUUID()) + oAccount.get().getName());
                return true;
            }
            sender.sendMessage(SweetieLib.NO_PERMISSION);
            return true;
        }
        Optional<Account> oAccount = getAccount(targetPlayer, false);
        if (!oAccount.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Could not find the player " + ChatColor.RESET + targetPlayer + ChatColor.RED + " in the database!");
            return true;
        }
        api.addNote(ConsoleAccount.getInstance(), oAccount.get(), note);
        sender.sendMessage(ChatColor.RED + "Added the note " + ChatColor.RESET + note + ChatColor.RED + " to " + ChatColor.RESET + chat.getCompletePlayerPrefix(oAccount.get().getUUID()) + oAccount.get().getName());
        return true;
    }
}
