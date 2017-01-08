package org.sweetiebelle.mcprofiler.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.sweetiebelle.mcprofiler.sponge.BaseCommand;
import org.sweetiebelle.mcprofiler.sponge.CommandSupplementSponge;
import org.sweetiebelle.mcprofiler.sponge.MCProfilerPlugin;

public class Help extends BaseCommand {
    public Help(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        args.checkPermission(sender, "mcprofiler.help");
        sendMessage(sender, "§bMCProfiler help menu:");
        sendMessage(sender, "§c/MCProfiler addnote <playerName> <note> §f - Adds a note to the given player");
        sendMessage(sender, "§c/MCProfiler readnotes <playerName> §f - Displays the notes on the given player");
        sendMessage(sender, "§c/MCProfiler info <playerName|uuid> §f - Displays a summary of the player");
        sendMessage(sender, "§c/status <playername|uuid> §f - short for /MCProfiler info");
        sendMessage(sender, "§c/MCProfiler lookup <ip> §f - Displays all accounts linked to the given IP");
        sendMessage(sender, "§c/MCProfiler listlinks [-r] <playerName> §f - Displays all accounts that might be linked to the given user. Use the -r flag for recursive player searching. This displays all of the alts of the player's alts, and all the alts of those alts....");
        sendMessage(sender, "§c/MCProfiler listips <playerName> §f - Lists all known IPs from a given player");
        sendMessage(sender, "§c/MCProfiler uuid <uuid> §f - Displays a username based on a UUID.");
        sendMessage(sender, "§c/MCProfiler maintenance <fixnotes | forcemakeaccount | updatename> <args> §f - Performs maintence commands See /MCProfiler maintenance with no args for help.");
        sendMessage(sender, "§c/MCProfiler reload§f - Reloads general configuration settings.");
        return CommandResult.success();
    }
}
