package net.shonx.mcprofiler.command.handler;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.shonx.lib.permission.PermissionManager;
import net.shonx.mcprofiler.API;
import net.shonx.mcprofiler.MCProfiler;
import net.shonx.mcprofiler.command.NoteCommand;

import com.google.common.base.Joiner;

public class NoteCommandHandler extends AbstractCommandHandler {

    private NoteCommand noteCommand;

    public NoteCommandHandler(MCProfiler plugin, API api, PermissionManager chat) {
        noteCommand = new NoteCommand(plugin, api, chat);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("note"))
            if (args.length >= 2) {
                String targetPlayer = args[0];
                String note = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
                return noteCommand.execute(sender, targetPlayer, note);
            }
        return false;
    }
}
