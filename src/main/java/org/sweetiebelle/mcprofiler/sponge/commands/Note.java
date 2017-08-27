package org.sweetiebelle.mcprofiler.sponge.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.sweetiebelle.mcprofiler.sponge.BaseCommand;
import org.sweetiebelle.mcprofiler.sponge.CommandSupplementSponge;
import org.sweetiebelle.mcprofiler.sponge.MCProfilerPlugin;

public class Note extends BaseCommand {

    public Note(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        args.checkPermission(sender, "mcprofiler.addnote");
        Optional<String> op = null;
        String playername = null;
        op = args.<String>getOne("player");
        if (op.isPresent())
            playername = op.get();
        if (playername == null)
            throw new CommandException(Text.of("Paramater player is empty."));
        String note = null;
        op = args.<String>getOne("note");
        if (op.isPresent())
            note = op.get();
        if (note == null)
            throw new CommandException(Text.of("Paramater note is empty."));
        if (sender instanceof Player)
            return cs.addNoteToPlayer(playername, sender.getName(), note, sender) ? CommandResult.success() : CommandResult.empty();
        return cs.addNoteToPlayer(playername, "Console", note, sender) ? CommandResult.success() : CommandResult.empty();
    }
}
