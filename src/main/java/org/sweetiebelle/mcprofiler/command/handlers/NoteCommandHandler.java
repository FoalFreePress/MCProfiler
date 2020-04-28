package org.sweetiebelle.mcprofiler.command.handlers;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.command.NoteCommand;

import com.google.common.base.Joiner;

public class NoteCommandHandler extends AbstractCommandHandler {

    private NoteCommand noteCommand;

    public NoteCommandHandler(API api, LuckPermsManager chat) {
        noteCommand = new NoteCommand(api, chat);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("note"))
            if (args.length >= 2) {
                String targetPlayer = args[0];
                String note = Joiner.on(' ').join(ArrayUtils.subarray(args, 1, args.length));
                return noteCommand.execute(sender, targetPlayer, note);
            }
        return false;
    }
}
