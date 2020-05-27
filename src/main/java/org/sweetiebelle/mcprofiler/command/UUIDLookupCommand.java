package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.Permission;

public class UUIDLookupCommand extends AbstractCommand {

    public UUIDLookupCommand(MCProfiler plugin, API api, PermissionManager manager) {
        super(plugin, api, manager);
    }

    public boolean execute(CommandSender pSender, String playerUUID) {
        Permission perm = new Permission(pSender);
        if (perm.canUseUUID()) {
            UUID uuid;
            try {
                uuid = UUID.fromString(playerUUID);
            } catch (IllegalArgumentException e) {
                pSender.sendMessage(ChatColor.RED + "That is not a UUID.");
                return true;
            }
            CompletableFuture<Optional<Account>> ao = api.getAccount(uuid);
            ao.thenAccept((oAccount) -> {
                if (oAccount.isPresent()) {
                    Account a = oAccount.get();
                    sendMessage(pSender, ChatColor.AQUA + "* " + chat.getCompletePlayerPrefix(a.getUUID()) + a.getName());
                    sendMessage(pSender, ChatColor.AQUA + "* [" + a.getUUID().toString() + "]");
                    return;
                }
                sendMessage(pSender, ChatColor.RED + "No player with UUID " + ChatColor.RESET + playerUUID + ChatColor.RED + " could be found.");
                return;
            });
            return true;
        }
        pSender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
