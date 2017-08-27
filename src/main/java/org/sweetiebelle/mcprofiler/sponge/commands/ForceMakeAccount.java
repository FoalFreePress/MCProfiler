package org.sweetiebelle.mcprofiler.sponge.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.sweetiebelle.mcprofiler.sponge.CommandSupplementSponge;
import org.sweetiebelle.mcprofiler.sponge.MCProfilerPlugin;

public class ForceMakeAccount extends Maintenance {

    public ForceMakeAccount(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) throws CommandException {
        args.checkPermission(sender, "mcprofiler.maintenance");
        final String[] str = new String[5];
        str[0] = "MCProfiler";
        str[1] = "forcemakeaccount";
        String uuid = null;
        String playerName = null;
        String ip = null;
        Optional<String> op;
        op = args.<String>getOne("uuid");
        if (op.isPresent())
            uuid = op.get();
        op = args.<String>getOne("lastKnownName");
        if (op.isPresent())
            playerName = op.get();
        op = args.<String>getOne("ip");
        if (op.isPresent())
            ip = op.get();
        str[2] = uuid;
        str[3] = playerName;
        str[4] = ip;
        return cs.maintenance(str, sender) ? CommandResult.success() : CommandResult.empty();
    }
}
