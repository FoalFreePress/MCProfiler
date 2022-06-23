package net.shonx.mcprofiler.command;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.shonx.lib.SweetieLib;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.API;
import net.shonx.mcprofiler.MCProfiler;
import net.shonx.mcprofiler.api.account.Account;
import net.shonx.mcprofiler.api.account.Note;
import net.shonx.mcprofiler.api.account.Permission;

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
