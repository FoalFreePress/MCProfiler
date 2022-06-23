package net.shonx.mcprofiler.command;

import java.util.concurrent.CompletableFuture;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.shonx.lib.SweetieLib;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.API;
import net.shonx.mcprofiler.MCProfiler;
import net.shonx.mcprofiler.api.account.Account;
import net.shonx.mcprofiler.api.account.Permission;

public class LookupIPCommand extends AbstractCommand {

    public LookupIPCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
    }


    public boolean execute(CommandSender sender, String ip) {
        Permission perm = new Permission(sender);
        if (perm.canLookupIP()) {
            // Find the uuids linked to the ip
            CompletableFuture<Account[]> future = api.getAccounts(ip);
            future.thenAccept((accounts) -> {
                if (accounts.length == 0) {
                    sendMessage(sender, ChatColor.RED + "Didn't find any accounts linked to the ip " + ChatColor.RESET + ip + ChatColor.RED + "!");
                    return;
                }
                // Get all the uuids and iterate over them
                sendMessage(sender, ChatColor.RED + "The player accounts linked to the ip " + ChatColor.RESET + ip + ChatColor.RED + " are:");
                for (Account a : accounts) {
                    sendMessage(sender, ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName());
                    sendMessage(sender, ChatColor.AQUA + "* [" + a.getUUID() + "]");
                }
                return;
            });
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
