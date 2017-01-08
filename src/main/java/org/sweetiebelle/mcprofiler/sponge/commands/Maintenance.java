package org.sweetiebelle.mcprofiler.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.sweetiebelle.mcprofiler.sponge.BaseCommand;
import org.sweetiebelle.mcprofiler.sponge.CommandSupplementSponge;
import org.sweetiebelle.mcprofiler.sponge.MCProfilerPlugin;

public class Maintenance extends BaseCommand {
    public Maintenance(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        args.checkPermission(sender, "mcprofiler.maintenance");
        sendMessage(sender, "&c/MCProfiler maintenance fixnotes <UUID> <name>  &f - Associates a playername with the UUID.");
        sendMessage(sender, "&c/MCProfiler maintenance forcemakeaccount UUID lastKnownName IP &f - Forces an account to be made for /MCProfiler info. If you don't know the IP, type in NULL.");
        sendMessage(sender, "&c/MCProfiler maintenance updatename UUID newname &f - Forces an account to be updated with the new name.");
        sendMessage(sender, "&cIf &f-1&c rows are affected, then there was an error performing the query.");
        return CommandResult.success();
    }
}
