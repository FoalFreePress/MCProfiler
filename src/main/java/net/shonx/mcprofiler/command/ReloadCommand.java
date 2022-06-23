package net.shonx.mcprofiler.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.shonx.lib.SweetieLib;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.API;
import net.shonx.mcprofiler.MCProfiler;
import net.shonx.mcprofiler.Settings;
import net.shonx.mcprofiler.api.account.Permission;

public class ReloadCommand extends AbstractCommand {

    private Settings settings;

    public ReloadCommand(MCProfiler plugin, Settings settings, API api, PermissionManager manager) {
        super(plugin, api, manager);
        this.settings = settings;
    }

    public boolean execute(CommandSender sender) {
        Permission perm = new Permission(sender);
        if (perm.canReload()) {
            sendMessage(sender, ChatColor.RED + "Reloading plugin...");
            settings.reloadSettings();
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
