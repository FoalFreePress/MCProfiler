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

public class ListIPs extends BaseCommand {

    public ListIPs(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        args.checkPermission(sender, "mcprofiler.listips");
        String player = null;
        final Optional<String> op = args.<String>getOne("player");
        if (op.isPresent())
            player = op.get();
        if (player == null)
            throw new CommandException(Text.of("Paramater player is empty."));
        return cs.getIPsByPlayer(player, sender) ? CommandResult.success() : CommandResult.empty();
    }
}
