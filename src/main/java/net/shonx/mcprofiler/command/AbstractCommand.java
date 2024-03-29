package net.shonx.mcprofiler.command;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.command.CommandSender;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.API;
import net.shonx.mcprofiler.MCProfiler;
import net.shonx.mcprofiler.api.account.Account;

public abstract class AbstractCommand {

    protected API api;
    protected PermissionManager chat;
    protected MCProfiler plugin;

    public AbstractCommand(MCProfiler plugin, API api, PermissionManager chat) {
        this.plugin = plugin;
        this.api = api;
        this.chat = chat;
    }

    /**
     * Worker class for getting an Account from a name or UUID.
     *
     * @param nameoruuid
     * @param needsLastTime
     * @return
     */
    protected CompletableFuture<Optional<Account>> getAccount(String nameoruuid, boolean needsLastNames) {
        try {
            return api.getAccount(UUID.fromString(nameoruuid), needsLastNames);
        } catch (IllegalArgumentException e) {
            return api.getAccount(nameoruuid, needsLastNames);
        }
    }


    protected void sendMessage(CommandSender sender, String message) {
        // Async messages are actually fine lol
        sender.sendMessage(message);
    }
}
