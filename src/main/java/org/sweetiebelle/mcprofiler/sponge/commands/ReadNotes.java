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

public class ReadNotes extends BaseCommand {

    public ReadNotes(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        String playername = null;
        Optional<String> op = null;
        op = args.<String>getOne("player");
        if (op.isPresent())
            playername = op.get();
        if (playername == null)
            throw new CommandException(Text.of("Paramater player is empty."));
        return cs.readNoteForPlayer(playername, sender) ? CommandResult.success() : CommandResult.empty();
    }
}
