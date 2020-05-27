package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.alternate.UUIDAlt;

public class ShowAltsCommand extends AbstractCommand {

    public ShowAltsCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
    }


    @SuppressWarnings("deprecation")
    public boolean execute(CommandSender sender, String targetName, boolean recursive) {
        if (sender.hasPermission("mcprofiler.listlinks")) {
            CompletableFuture<Optional<Account>> future = getAccount(targetName, false);
            future.thenAccept((oAccount) -> {
                if (oAccount.isPresent()) {
                    Account a = oAccount.get();
                    CompletableFuture<UUIDAlt[]> futureAlts = api.getAccounts(a.getUUID(), recursive);
                    futureAlts.thenAccept((altAccounts) -> {
                        if (altAccounts.length == 0) {
                            sendMessage(sender, ChatColor.RED + "No known alts of " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName());
                            return;
                        }
                        sendMessage(sender, ChatColor.RED + "The player " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName() + " " + ChatColor.RED + "(" + ChatColor.RESET + a.getIP() + ChatColor.RED + ") has the following associated accounts:");
                        for (UUIDAlt alt : altAccounts) {
                            Account fullAlt = api.getAccountNoFuture(alt.getUUID());
                            sendMessage(sender, ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(alt.getUUID()) + fullAlt.getName() + " " + ChatColor.RED + "(" + ChatColor.RESET + alt.getIP() + ChatColor.RED + ")");
                        }
                        return;
                    });
                    return;
                }
                sendMessage(sender, ChatColor.RED + "Couldn't find the player " + ChatColor.RESET + targetName + ChatColor.RED + " in the database!");
                return;
            });
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
