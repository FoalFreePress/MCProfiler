package org.sweetiebelle.mcprofiler.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.sweetiebelle.mcprofiler.sponge.commands.AddNote;
import org.sweetiebelle.mcprofiler.sponge.commands.FixNotes;
import org.sweetiebelle.mcprofiler.sponge.commands.ForceMakeAccount;
import org.sweetiebelle.mcprofiler.sponge.commands.Help;
import org.sweetiebelle.mcprofiler.sponge.commands.Info;
import org.sweetiebelle.mcprofiler.sponge.commands.ListIPs;
import org.sweetiebelle.mcprofiler.sponge.commands.ListLinks;
import org.sweetiebelle.mcprofiler.sponge.commands.Lookup;
import org.sweetiebelle.mcprofiler.sponge.commands.Maintenance;
import org.sweetiebelle.mcprofiler.sponge.commands.NewName;
import org.sweetiebelle.mcprofiler.sponge.commands.Note;
import org.sweetiebelle.mcprofiler.sponge.commands.ReadNotes;
import org.sweetiebelle.mcprofiler.sponge.commands.Reload;
import org.sweetiebelle.mcprofiler.sponge.commands.Status;
import org.sweetiebelle.mcprofiler.sponge.commands.UUID;

public class CommandHandler extends BaseCommand {
    CommandHandler(final MCProfilerPlugin p, final CommandSupplementSponge cs) {
        super(p, cs);
        Sponge.getCommandManager().register(p, CommandSpec.builder().description(Text.of("Core command for MCProfiler. Use /MCProfiler help for info")).child(CommandSpec.builder().permission("mcprofiler.help").description(Text.of("Prints help.")).executor(new Help(p, cs)).build(), "help").child(CommandSpec.builder().permission("mcprofiler.addnote").description(Text.of("Prints help.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player"))), GenericArguments.remainingJoinedStrings(Text.of("note"))).executor(new AddNote(p, cs)).build(), "addnote").child(CommandSpec.builder().permission("mcprofiler.info.basic").description(Text.of("Displays a summary of the player")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player")))).executor(new Info(p, cs)).build(), "info").child(CommandSpec.builder().permission("mcprofiler.readnotes").description(Text.of("Displays the notes on the given player")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player")))).executor(new ReadNotes(p, cs)).build(), "readnotes").child(CommandSpec.builder().permission("mcprofiler.lookup").description(Text.of("Displays all accounts linked to the given IP")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("ip")))).executor(new Lookup(p, cs)).build(), "lookup").child(CommandSpec.builder().permission("mcprofiler.listlinks").description(Text.of("Displays all accounts that might be linked to the given user. Use the -r flag for recursive player searching. This displays all of the alts of the player's alts, and all the alts of those alts....")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("isRecursive"))), GenericArguments.optional(GenericArguments.string(Text.of("player")))).executor(new ListLinks(p, cs)).build(), "listlinks").child(CommandSpec.builder().permission("mcprofiler.listips").description(Text.of("Lists all known IPs from a given player")).arguments(GenericArguments.optional(GenericArguments.string(Text.of("player")))).executor(new ListIPs(p, cs)).build(), "listips").child(CommandSpec.builder().permission("mcprofiler.uuid").description(Text.of("Displays a username based on a UUID.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("uuid")))).executor(new UUID(p, cs)).build(), "uuid").child(CommandSpec.builder().permission("mcprofiler.maintenance").description(Text.of("Performs maintence commands See /MCProfiler maintenance with no args for help.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("uuid")))).child(CommandSpec.builder().permission("mcprofiler.maintenance").description(Text.of("Associates a playername with the UUID.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("uuid"))), GenericArguments.onlyOne(GenericArguments.string(Text.of("name")))).executor(new FixNotes(p, cs)).build(), "fixnotes").child(CommandSpec.builder().permission("mcprofiler.maintenance").description(Text.of("Forces an account to be made for /MCProfiler info. If you don't know the IP, type in NULL.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("uuid"))), GenericArguments.onlyOne(GenericArguments.string(Text.of("lastKnownName"))), GenericArguments.onlyOne(GenericArguments.string(Text.of("ip")))).executor(new ForceMakeAccount(p, cs)).build(), "forcemakeaccount").child(CommandSpec.builder().permission("mcprofiler.maintenance").description(Text.of("Forces an account to be updated with the new name.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("uuid"))), GenericArguments.onlyOne(GenericArguments.string(Text.of("newname")))).executor(new NewName(p, cs)).build()).executor(new Maintenance(p, cs)).build(), "maintenance").child(CommandSpec.builder().permission("mcprofiler.reload").description(Text.of("Reloads general configuration settings.")).executor(new Reload(p, cs)).build(), "reload").executor(this).build(), "MCProfiler", "MCP");
        Sponge.getCommandManager().register(p, CommandSpec.builder().description(Text.of("Commands for printing player info.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player")))).executor(new Status(p, cs)).build(), "status");
        Sponge.getCommandManager().register(p, CommandSpec.builder().description(Text.of("Add a note to a player.")).arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("player"))), GenericArguments.onlyOne(GenericArguments.remainingJoinedStrings(Text.of("note")))).executor(new Note(p, cs)).build(), "note");
    }

    @Override
    public CommandResult execute(final CommandSource sender, final CommandContext args) {
        sender.sendMessage(Text.of(TextColors.RED, "Not enough arguments!"));
        sender.sendMessage(Text.of(TextColors.RED, "Usage: /mcprofiler help|lookup|reload|addnote|listlinks|readnotes|uuid|maintenance|info| <params>"));
        return CommandResult.success();
    }
}