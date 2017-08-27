package org.sweetiebelle.mcprofiler.sponge;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public abstract class BaseCommand implements CommandExecutor {

    public CommandSupplementSponge cs;
    public MCProfilerPlugin p;

    public BaseCommand(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        this.p = p;
        this.cs = cs;
    }

    public void sendMessage(final CommandSource sender, final String... msg) {
        for (final String str : msg)
            sender.sendMessage(Text.of(TextSerializers.FORMATTING_CODE.deserializeUnchecked(str)));
    }
}
