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
import net.shonx.mcprofiler.api.account.Permission;

public class ListIPsCommand extends AbstractCommand {

    public ListIPsCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
    }

    public boolean execute(CommandSender sender, String playerName) {
        Permission perm = new Permission(sender);
        if (perm.canListIPs()) {
            CompletableFuture<Optional<Account>> future = getAccount(playerName, false);
            future.thenAccept((oAccount) -> {
                if (!oAccount.isPresent()) {
                    sendMessage(sender, ChatColor.RED + "Could not find the player " + ChatColor.RESET + playerName + ChatColor.RED + " in the database!");
                    return;
                }
                Account a = oAccount.get();
                CompletableFuture<String[]> futureIps = api.getIPs(a);
                futureIps.thenAccept((ips) -> {
                    sendMessage(sender, ChatColor.RED + "The ips linked to the player " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName() + ChatColor.RESET + ChatColor.RED + " are:");
                    for (String ip : ips)
                        sendMessage(sender, ChatColor.AQUA + "* " + ip);
                });
                return;
            });
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}