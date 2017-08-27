package org.sweetiebelle.mcprofiler.sponge;

import java.util.ArrayList;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.sweetiebelle.mcprofiler.Account;
import org.sweetiebelle.mcprofiler.BaseAccount;
import org.sweetiebelle.mcprofiler.CommandSupplement;
import org.sweetiebelle.mcprofiler.Data;
import org.sweetiebelle.mcprofiler.NamesFetcher.Response;
import org.sweetiebelle.mcprofiler.NoDataException;
import org.sweetiebelle.mcprofiler.Settings;
import org.sweetiebelle.mcprofiler.UUIDAlt;

public class CommandSupplementSponge extends CommandSupplement<CommandSource> {

    public CommandSupplementSponge(final Settings settings, final Data data, final MCProfilerPlugin mcProfilerPlugin) {
        super(settings, data);
    }

    @Override
    protected Account getAccount(final String name, final boolean needsLastTime) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(name);
        } catch (final IllegalArgumentException e) {
            uuid = null;
            d.error(e);
        }
        if (uuid == null) {
            for (final Player p : Sponge.getGame().getServer().getOnlinePlayers())
                if (p.getName().equalsIgnoreCase(name))
                    return d.getAccount(p.getUniqueId(), needsLastTime);
            return d.getAccount(name, needsLastTime);
        }
        return d.getAccount(uuid, needsLastTime);
    }

    @Override
    protected boolean hasPermission(final CommandSource sender, final String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(final CommandSource sender, final String... msg) {
        for (final String str : msg)
            sender.sendMessage(Text.of(TextSerializers.FORMATTING_CODE.deserializeUnchecked(str)));
    }

    @Override
    protected String getPrefix(final UUID uuid) {
        return "&b";
    }

    @Override
    public boolean displayPlayerInformation(final String playername, final CommandSource pSender) {
        final Account a = getAccount(playername, true);
        if (hasPermission(pSender, "mcprofiler.info.basic.name")) {
            // Get the UUID for the given player and read the note
            if (a == null) {
                sendMessage(pSender, "&cCould not find the player '&f" + playername + "&c' in the database!");
                return true;
            }
            // Print a "summary" of the given player
            sendMessage(pSender, "&b* " + getPrefix(a.getUUID()) + a.getName());
            if (hasPermission(pSender, "mcprofiler.info.basic.uuid"))
                sendMessage(pSender, "&b* [" + a.getUUID().toString() + "]");
            if (hasPermission(pSender, "mcprofiler.info.basic.previoususernames"))
                displayPreviousUsernames(a, pSender);
        }
        // Check if the player is online or not
        boolean isOnline = false;
        boolean senderCanSee = true;
        Player queriedPlayer = null;
        if (pSender instanceof Player) {
            senderCanSee = false;
            for (final Player player : Sponge.getGame().getServer().getOnlinePlayers())
                if (player.getName().equals(a.getName())) {
                    isOnline = true;
                    senderCanSee = true;
                    queriedPlayer = player;
                    break;
                }
        } else {
            senderCanSee = true;
            for (final Player player : Sponge.getGame().getServer().getOnlinePlayers())
                if (player.getName().equals(a.getName())) {
                    isOnline = true;
                    queriedPlayer = player;
                    break;
                }
        }
        if (hasPermission(pSender, "mcprofiler.info.online"))
            if (isOnline && senderCanSee)
                sendMessage(pSender, "&c- &fLast on: &aOnline now");
            else
                sendMessage(pSender, "&c- &fLast on: &9" + a.getLastOn());
        if (hasPermission(pSender, "mcprofiler.info.ip"))
            // Get the player IP address and notes
            sendMessage(pSender, "&c- &fPeer address: &9" + a.getIP());
        // Notes will never be null. Data.java.293 & 232
        if (hasPermission(pSender, "mcprofiler.readnotes"))
            sendMessage(pSender, a.getNotes());
        // Get the position of the player
        if (hasPermission(pSender, "mcprofiler.info.position"))
            if (isOnline && senderCanSee && queriedPlayer != null)
                sendMessage(pSender, "&c- &fLocation: &9" + getLocation(queriedPlayer.getLocation()));
            else if (a.getLocation() != null)
                sendMessage(pSender, "&c- &fLocation: &9" + a.getLocation());
            else
                sendMessage(pSender, "&c- &fLocation: &9null");
        return true;
    }

    private String getLocation(final Location<World> location) {
        return String.format("%d,%d,%d:%s", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getExtent().getName());
    }

    @Override
    public void displayPreviousUsernames(final Account a, final CommandSource sender) {
        final Response[] previousUsernames = a.getPreviousNames();
        try {
            if (previousUsernames == null)
                throw new NoDataException("Previous Usernames returned null!");
            sendMessage(sender, "&cPlayer " + a.getName() + " has the known previous usernames:");
            for (final Response response : previousUsernames) {
                String time = getTimeStamp(response.changedToAt / 1000L);
                if (response.changedToAt == 0L)
                    time = "Original Name      ";
                sendMessage(sender, "&b " + time + " " + response.name);
            }
        } catch (final NoDataException e) {
            d.error(e);
            sendMessage(sender, "&cAn internal error occured while performing this command.");
        }
    }

    @Override
    public void displayPossiblePlayerAlts(final Account a, final BaseAccount[] altAccounts, final CommandSource pSender, final boolean recursive) {
        boolean first = true;
        if (altAccounts == null)
            throw new NullPointerException("Account list cannot be null!");
        if (recursive) {
            for (final BaseAccount alt : altAccounts) {
                if (first)
                    sendMessage(pSender, "&cThe player " + getPrefix(a.getUUID()) + a.getName() + " &c(&f" + a.getIP() + "&c) has the following associated accounts:");
                first = false;
                sendMessage(pSender, "&b* " + getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).getName() + " &c(&f" + alt.getIP() + "&c)");
            }
            if (first)
                sendMessage(pSender, "&cNo known alts of that Account.");
        } else {
            final ArrayList<UUIDAlt> alreadypassed = new ArrayList<UUIDAlt>(altAccounts.length);
            for (final BaseAccount altAccount : altAccounts) {
                final UUIDAlt alt = (UUIDAlt) altAccount;
                if (alt.getUUID().equals(a.getUUID()) || alreadypassed.contains(alt))
                    continue;
                if (first)
                    sendMessage(pSender, "&cThe player " + getPrefix(a.getUUID()) + a.getName() + " &c(&f" + a.getIP() + "&c) has the following associated accounts:");
                first = false;
                sendMessage(pSender, "&b* " + getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).getName() + " &c(&f" + alt.getIP() + "&c)");
                alreadypassed.add(alt);
            }
            if (first)
                sendMessage(pSender, "&cNo known alts of that Account.");
        }
    }

    @Override
    public void notifyStaffOfPossibleAlts(final UUID puuid, final String name, final BaseAccount[] baseAccounts) {
        // Need valid data
        if (d.isNull(name) || baseAccounts == null)
            return;
        // Build the string to display
        String string = getPrefix(puuid) + name + " &fmight be ";
        final ArrayList<UUIDAlt> alreadyadded = new ArrayList<UUIDAlt>(baseAccounts.length);
        for (final BaseAccount altAccount : baseAccounts) {
            final UUIDAlt alt = BaseAccount.switchType(UUIDAlt.class, altAccount);
            if (alt == null)
                return;
            final UUID uuid = alt.getUUID();
            if (uuid.equals(puuid) || alreadyadded.contains(alt))
                continue;
            final Account a = d.getAccount(uuid, false);
            // if (bc.isBanned(uuid))
            // string += getPrefix(a.getUUID()) + a.getName() + " &7(BANNED) &c";
            // else
            string += getPrefix(a.getUUID()) + a.getName();
            string += "&f, ";
            alreadyadded.add(alt);
        }
        final String compare = getPrefix(puuid) + name + " &fmight be ";
        // Again, their only alt is themselves.
        if (string.equalsIgnoreCase(compare))
            return;
        // Fix the tailing comma
        string = string.substring(0, string.length() - 4);
        // Find all players that should be notified and notify them
        for (final Player admin : Sponge.getServer().getOnlinePlayers())
            if (admin.hasPermission("mcprofiler.notifiedofalts"))
                admin.sendMessage(Text.of(TextSerializers.FORMATTING_CODE.deserializeUnchecked(string)));
    }
}
