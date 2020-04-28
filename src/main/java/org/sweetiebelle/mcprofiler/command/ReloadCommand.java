package org.sweetiebelle.mcprofiler.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.Settings;

public class ReloadCommand extends AbstractCommand {

    private Settings settings;

    public ReloadCommand(Settings settings, API api, LuckPermsManager manager) {
        super(api, manager);
        this.settings = settings;
    }

    public boolean execute(CommandSender sender) {
        if (sender.hasPermission("mcprofiler.reload")) {
            sender.sendMessage(ChatColor.RED + "Reloading plugin...");
            settings.reloadSettings();
            return true;
        }
        sender.sendMessage(SweetieLib.NO_PERMISSION);
        return true;
    }
}
