package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.ConsoleAccount;
import org.sweetiebelle.mcprofiler.api.account.Permission;

public class NoteCommand extends AbstractCommand {

    public NoteCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
    }

    @SuppressWarnings("deprecation")
    public boolean execute(CommandSender sender, String targetPlayer, String note) {
        Permission perm = new Permission(sender);
        if (sender instanceof Player) {
            if (perm.canAddNote()) {
                addNoteToPlayer(sender, api.getAccountNoFuture(((Player) sender).getUniqueId()), targetPlayer, note);
                return true;
            }
            sender.sendMessage(SweetieLib.NO_PERMISSION);
            return true;
        }
        addNoteToPlayer(sender, ConsoleAccount.getInstance(), targetPlayer, note);
        return true;
    }

    private void addNoteToPlayer(CommandSender commandSender, Account sender, String targetPlayer, String note) {
        CompletableFuture<Optional<Account>> future = getAccount(targetPlayer, false);
        future.thenAccept((oAccount) -> {
            if (!oAccount.isPresent()) {
                sendMessage(commandSender, ChatColor.RED + "Could not find the player " + ChatColor.RESET + targetPlayer + ChatColor.RED + " in the database!");
                return;
            }
            api.addNote(sender, oAccount.get(), note).thenAccept((object) -> {
                sendMessage(commandSender, ChatColor.RED + "Added the note " + ChatColor.RESET + note + ChatColor.RED + " to " + ChatColor.RESET + chat.getCompletePlayerPrefix(oAccount.get().getUUID()) + oAccount.get().getName());
            });
            return;
        });
    }
}
