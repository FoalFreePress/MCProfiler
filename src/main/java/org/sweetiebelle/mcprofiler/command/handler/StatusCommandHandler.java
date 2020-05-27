package org.sweetiebelle.mcprofiler.command.handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.Settings;
import org.sweetiebelle.mcprofiler.command.StatusCommand;

public class StatusCommandHandler extends AbstractCommandHandler {

    private StatusCommand statusCommand;

    public StatusCommandHandler(MCProfiler plugin, Settings settings, API api, PermissionManager chat) {
        statusCommand = new StatusCommand(plugin, settings, api, chat);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length == 1)
                return statusCommand.execute(sender, args[0]);
            return false;
        }
        return false;
    }
}
