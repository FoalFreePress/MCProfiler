package net.shonx.mcprofiler.command;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.API;
import net.shonx.mcprofiler.MCProfiler;
import net.shonx.mcprofiler.Settings;
import net.shonx.mcprofiler.api.IBans;
import net.shonx.mcprofiler.api.account.Account;
import net.shonx.mcprofiler.api.account.Permission;
import net.shonx.mcprofiler.api.account.alternate.BaseAccount;
import net.shonx.mcprofiler.api.account.alternate.UUIDAlt;
import net.shonx.mcprofiler.api.exception.BanPluginNotLoadedException;
import net.shonx.mcprofiler.controller.ban.BrohoofBans;
import net.shonx.mcprofiler.controller.ban.BukkitBans;

public class NotifyStaffCommand extends AbstractCommand {

    private IBans bc;
    private Settings settings;

    public NotifyStaffCommand(MCProfiler plugin, Settings settings, API api, PermissionManager manager) {
        super(plugin, api, manager);
        this.settings = settings;
        try {
            bc = new BrohoofBans();
        } catch (BanPluginNotLoadedException ex) {
            plugin.getLogger().info(ChatColor.RED + "I couldn't find BrohoofBans!");
            bc = new BukkitBans();
        }
    }

    @SuppressWarnings("deprecation")
    public void execute(CompletableFuture<Optional<Account>> futureAccount, CompletableFuture<UUIDAlt[]> futureAlts) {
        futureAccount.thenAccept((oAccount) -> {
            Account account = oAccount.get();
            UUIDAlt[] altAccounts;
            try {
                altAccounts = futureAlts.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // No Accounts
            if (altAccounts.length == 0)
                return;
            // Build the string to display
            String string = chat.getCompletePlayerPrefix(account.getUUID()) + account.getName() + " " + ChatColor.RESET + "might be ";
            for (BaseAccount altAccount : altAccounts) {
                UUIDAlt alt = new UUIDAlt(altAccount.getUUID(), altAccount.getIP());
                Account other = api.getAccountNoFuture(alt.getUUID());
                if (bc.isBannedDangerous(alt.getUUID()))
                    string += chat.getCompletePlayerPrefix(other.getUUID()) + other.getName() + " " + ChatColor.GRAY + "(BANNED) " + ChatColor.RED + "";
                else
                    string += chat.getCompletePlayerPrefix(other.getUUID()) + other.getName();
                string += ChatColor.RESET + ", ";
            }
            // Fix the tailing comma
            final String message = string.substring(0, string.length() - 4);
            // Find all players that should be notified and notify them
            for (Player admin : Bukkit.getServer().getOnlinePlayers()) {
                Permission perm = new Permission(admin);
                if (perm.canSeeAltsOnPlayerJoin())
                    sendMessage(admin, message);
            }
            if(settings.sendAltsToConsole)
                Bukkit.getConsoleSender().sendMessage(message);
        });
    }
}
