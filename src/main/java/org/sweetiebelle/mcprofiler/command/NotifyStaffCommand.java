package org.sweetiebelle.mcprofiler.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.BansController;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.alternate.BaseAccount;
import org.sweetiebelle.mcprofiler.api.account.alternate.UUIDAlt;

public class NotifyStaffCommand extends AbstractCommand {

    private BansController bc;

    public NotifyStaffCommand(API api, LuckPermsManager manager) {
        super(api, manager);
        bc = new BansController();
    }

    public void execute(Account account, UUIDAlt[] baseAccounts) {
        // No Accounts
        if(baseAccounts.length == 0)
            return;
        // Build the string to display
        String string = chat.getCompletePlayerPrefix(account.getUUID()) + account.getName() + " " + ChatColor.RESET + "might be ";
        for (BaseAccount altAccount : baseAccounts) {
            UUIDAlt alt = new UUIDAlt(altAccount.getUUID(), altAccount.getIP());
            Account other = api.getAccount(alt.getUUID()).get();
            if (bc.isBanned(alt.getUUID()))
                string += chat.getCompletePlayerPrefix(other.getUUID()) + other.getName() + " " + ChatColor.GRAY + "(BANNED) " + ChatColor.RED + "";
            else
                string += chat.getCompletePlayerPrefix(other.getUUID()) + other.getName();
            string += ChatColor.RESET + ", ";
        }
        // Fix the tailing comma
        string = string.substring(0, string.length() - 4);
        // Find all players that should be notified and notify them
        for (Player admin : Bukkit.getServer().getOnlinePlayers())
            if (admin.hasPermission("mcprofiler.notifiedofalts"))
                admin.sendMessage(string);
        Bukkit.getConsoleSender().sendMessage(string);
    }
}
