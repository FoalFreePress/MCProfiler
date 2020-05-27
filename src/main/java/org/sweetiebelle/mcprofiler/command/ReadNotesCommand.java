package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.Note;
import org.sweetiebelle.mcprofiler.api.account.Permission;

public class ReadNotesCommand extends AbstractCommand {

    public ReadNotesCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
    }

    public boolean execute(CommandSender sender, String playerName) {
        Permission perm = new Permission(sender);
        if (perm.canReadNotes()) {
            CompletableFuture<Optional<Account>> future = getAccount(playerName, false);
            future.thenAccept((oAccount) -> {
                if (oAccount.isPresent()) {
                    Account account = oAccount.get();
                    BukkitTask task = fillNotes(account);
                    while (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()) || Bukkit.getScheduler().isQueued(task.getTaskId())) {
                        // Do nothing, wait for it to be done.
                    }
                    Note[] notes = account.getNotes();
                    if (notes.length == 0) {
                        sendMessage(sender, ChatColor.RED + "No notes were found.");
                    }
                    for (Note note : notes) {
                        sendMessage(sender, note.toString());
                    }
                } else
                    sendMessage(sender, ChatColor.RED + "Couldn't find the player " + ChatColor.RESET + playerName + ChatColor.RED + " in the database!");
                return;
            });
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
