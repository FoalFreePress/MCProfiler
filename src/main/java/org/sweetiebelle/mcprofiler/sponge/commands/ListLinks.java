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

public class ListLinks extends BaseCommand {

    public ListLinks(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        args.checkPermission(sender, "mcprofiler.listlinks");
        boolean recursive = false;
        String player = null;
        Optional<String> op = args.<String>getOne("isRecursive");
        if (op.isPresent())
            if (op.get().equalsIgnoreCase("-r"))
                recursive = true;
            else
                player = op.get();
        if (recursive) {
            op = args.<String>getOne("player");
            if (op.isPresent())
                player = op.get();
        }
        if (player == null)
            throw new CommandException(Text.of("Paramater player is empty."));
        return cs.findLinkedUsers(player, sender, recursive) ? CommandResult.success() : CommandResult.empty();
    }
}
