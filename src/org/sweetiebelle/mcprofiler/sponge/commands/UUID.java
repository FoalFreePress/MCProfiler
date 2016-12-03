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

public class UUID extends BaseCommand {
    public UUID(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        String uuid = null;
        final Optional<String> op = args.getOne("uuid");
        if (op.isPresent())
            uuid = op.get();
        if (uuid == null)
            throw new CommandException(Text.of("Paramater is empty."));
        return cs.getPlayerFromUUID(uuid, sender) ? CommandResult.success() : CommandResult.empty();
    }
}
