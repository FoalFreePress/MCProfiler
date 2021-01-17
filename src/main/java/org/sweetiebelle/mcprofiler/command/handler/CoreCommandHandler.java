package org.sweetiebelle.mcprofiler.command.handler;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.sweetiebelle.lib.permission.PermissionManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.Settings;
import org.sweetiebelle.mcprofiler.command.HelpCommand;
import org.sweetiebelle.mcprofiler.command.ListIPsCommand;
import org.sweetiebelle.mcprofiler.command.LookupIPCommand;
import org.sweetiebelle.mcprofiler.command.NoteCommand;
import org.sweetiebelle.mcprofiler.command.ReadNotesCommand;
import org.sweetiebelle.mcprofiler.command.ReloadCommand;
import org.sweetiebelle.mcprofiler.command.ShowAltsCommand;
import org.sweetiebelle.mcprofiler.command.StatusCommand;
import org.sweetiebelle.mcprofiler.command.UUIDLookupCommand;

import com.google.common.base.Joiner;

public class CoreCommandHandler extends AbstractCommandHandler {

    private HelpCommand helpCommand;
    private ListIPsCommand listIPsCommand;
    private LookupIPCommand lookupCommand;
    private NoteCommand noteCommand;
    private ReadNotesCommand readNotesCommand;
    private ReloadCommand reloadCommand;
    private ShowAltsCommand showAltsCommand;
    private StatusCommand statusCommand;
    private UUIDLookupCommand uuidCommand;

    public CoreCommandHandler(MCProfiler plugin, Settings settings, API api, PermissionManager chat) {
        helpCommand = new HelpCommand(plugin, api, chat);
        noteCommand = new NoteCommand(plugin, api, chat);
        lookupCommand = new LookupIPCommand(plugin, api, chat);
        readNotesCommand = new ReadNotesCommand(plugin, api, chat);
        statusCommand = new StatusCommand(plugin, api, chat);
        showAltsCommand = new ShowAltsCommand(plugin, api, chat);
        uuidCommand = new UUIDLookupCommand(plugin, api, chat);
        reloadCommand = new ReloadCommand(plugin, settings, api, chat);
        listIPsCommand = new ListIPsCommand(plugin, api, chat);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0)
            return false;
        switch (args[0].toLowerCase()) {
            case "help": {
                return helpCommand.execute(sender);
            }
            case "addnote": {
                if (args.length > 2) {
                    String targetName = args[1];
                    String note = Joiner.on(' ').join(ArrayUtils.subarray(args, 2, args.length));
                    return noteCommand.execute(sender, targetName, note);
                }
                return false;
            }
            case "lookup": {
                if (args.length == 2)
                    return lookupCommand.execute(sender, args[1]);
                return false;
            }
            case "readnotes": {
                if (args.length == 2)
                    return readNotesCommand.execute(sender, args[1]);
                return false;
            }
            case "info": {
                if (args.length == 2)
                    return statusCommand.execute(sender, args[1]);
                return false;
            }
            case "listlinks": {
                if (args.length == 2)
                    return showAltsCommand.execute(sender, args[1], false);
                if (args.length == 3 && args[1].equalsIgnoreCase("-r"))
                    return showAltsCommand.execute(sender, args[2], true);
                return false;
            }
            case "uuid": {
                if (args.length == 2)
                    return uuidCommand.execute(sender, args[1]);
                return false;
            }
            case "reload": {
                return reloadCommand.execute(sender);
            }
            case "listips": {
                if (args.length == 2)
                    return listIPsCommand.execute(sender, args[1]);
                return false;
            }
        }
        return false;
    }
}
