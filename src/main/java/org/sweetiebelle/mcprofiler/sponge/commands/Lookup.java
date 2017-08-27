package org.sweetiebelle.mcprofiler.sponge.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.sweetiebelle.mcprofiler.sponge.BaseCommand;
import org.sweetiebelle.mcprofiler.sponge.CommandSupplementSponge;
import org.sweetiebelle.mcprofiler.sponge.MCProfilerPlugin;

public class Lookup extends BaseCommand {

    public Lookup(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        args.checkPermission(sender, "mcprofiler.lookup");
        String ip = null;
        final Optional<String> op = args.<String>getOne("ip");
        if (op.isPresent())
            ip = op.get();
        if (ip == null)
            throw new CommandException(Text.of("Paramater ip is empty."));
        return cs.displayPlayersLinkedToIP(ip, sender) ? CommandResult.success() : CommandResult.empty();
    }
}
