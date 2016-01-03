package org.sweetiebelle.mcprofiler;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

/**
 * This class handles basic commands, but does not really do the bulk of the work.
 *
 */
class CommandHandler implements CommandExecutor {
    /**
     * The message to send people with no permission.
     */
    private final static String noPermission = ChatColor.RED + "You do not have permission.";
    /**
     * Our command supplement
     */
    private final CommandSupplement cs;

    /**
     * Constructs a new CommandHandler
     * @param cs
     */
    CommandHandler(final CommandSupplement cs) {
        this.cs = cs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // Handle short commands.
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length == 1)
                return cs.displayPlayerInformation(args[0], sender);
            return false;
        }
        if (command.getName().equalsIgnoreCase("note")) {
            if (args.length >= 2) {
                if (sender.hasPermission("mcprofiler.addnote")) {
                    final String note = Joiner.on(' ').join(ArrayUtils.subarray(args, 1, args.length));
                    if (sender instanceof Player)
                        return cs.addNoteToPlayer(args[0], sender.getName(), note, sender);
                    return cs.addNoteToPlayer(args[0], "Console", note, sender);
                }
                sender.sendMessage(noPermission);
                return true;
            }
            return false;
        }
        // Make sure there's enough arguments, if not, end here
        if (args.length == 0) {
            sender.sendMessage("Not enough arguments. Type /mcprofiler help for help.");
            return true;
        }
        // Figure out what command to execute
        final String instruction = args[0];
        boolean recursive = false;
        if (instruction.equals("help")) {
            if (sender.hasPermission("mcprofiler.help")) {
                sender.sendMessage("§bMCProfiler help menu:");
                sender.sendMessage("§c/MCProfiler addnote <playerName> <note> §f - Adds a note to the given player");
                sender.sendMessage("§c/MCProfiler readnotes <playerName> §f - Displays the notes on the given player");
                sender.sendMessage("§c/MCProfiler info <playerName|uuid> §f - Displays a summary of the player");
                sender.sendMessage("§c/status <playername|uuid> §f - short for /MCProfiler info");
                sender.sendMessage("§c/MCProfiler lookup <ip> §f - Displays all accounts linked to the given IP");
                sender.sendMessage("§c/MCProfiler listlinks [-r] <playerName> §f - Displays all accounts that might be linked to the given user. Use the -r flag for recursive player searching. This displays all of the alts of the player's alts, and all the alts of those alts....");
                sender.sendMessage("§c/MCProfiler listips <playerName> §f - Lists all known IPs from a given player");
                sender.sendMessage("§c/MCProfiler uuid <uuid> §f - Displays a username based on a UUID.");
                sender.sendMessage("§c/MCProfiler maintenance <fixnotes | forcemakeaccount> <args> §f - Performs maintence commands See /MCProfiler maintenance with no args for help.");
                sender.sendMessage("§c/MCProfiler reload§f - Reloads general configuration settings.");
                return true;
            }
            sender.sendMessage(noPermission);
            return true;
        }
        if (instruction.equals("addnote") && args.length >= 3) {
            if (sender.hasPermission("mcprofiler.addnote")) {
                final String note = Joiner.on(' ').join(ArrayUtils.subarray(args, 2, args.length));
                if (sender instanceof Player)
                    return cs.addNoteToPlayer(args[1], sender.getName(), note, sender);
                return cs.addNoteToPlayer(args[1], "Console", note, sender);
            }
            sender.sendMessage(noPermission);
            return true;
        }
        if (args.length >= 2)
            if (args[1].equalsIgnoreCase("-r"))
                recursive = true;
        if (instruction.equalsIgnoreCase("lookup") && args.length == 2)
            return cs.displayPlayersLinkedToIP(args[1], sender);
        if (instruction.equalsIgnoreCase("readnotes") && args.length == 2)
            return cs.readNoteForPlayer(args[1], sender);
        if (instruction.equalsIgnoreCase("info") && args.length == 2)
            return cs.displayPlayerInformation(args[1], sender);
        if (instruction.equalsIgnoreCase("listlinks") && (args.length == 2 || args.length == 3))
            return cs.findLinkedUsers(recursive ? args[2] : args[1], sender, recursive);
        if (instruction.equalsIgnoreCase("uuid") && args.length == 2)
            return cs.getPlayerFromUUID(args[1], sender);
        if (instruction.equalsIgnoreCase("reload") && args.length == 1)
            return cs.reloadSettings(sender);
        if (instruction.equalsIgnoreCase("listips") && args.length == 2)
            return cs.getIPsByPlayer(args[1], sender);
        if (instruction.equalsIgnoreCase("maintenance"))
            return cs.maintenance(args, sender);
        sender.sendMessage("§cInvalid instruction '" + instruction + "' or invalid number of parameters. Try to use /MCProfiler help");
        return true;
    }
}
