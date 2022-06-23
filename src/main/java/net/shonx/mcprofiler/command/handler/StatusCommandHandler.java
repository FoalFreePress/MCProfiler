package net.shonx.mcprofiler.command.handler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.API;
import net.shonx.mcprofiler.MCProfiler;
import net.shonx.mcprofiler.command.StatusCommand;

public class StatusCommandHandler extends AbstractCommandHandler {

    private StatusCommand statusCommand;

    public StatusCommandHandler(MCProfiler plugin, API api, PermissionManager chat) {
        statusCommand = new StatusCommand(plugin, api, chat);
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
