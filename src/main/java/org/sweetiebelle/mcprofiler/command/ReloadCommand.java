package org.sweetiebelle.mcprofiler.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.Settings;
import org.sweetiebelle.mcprofiler.api.account.Permission;

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
