package org.sweetiebelle.mcprofiler.bukkit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.sweetiebelle.mcprofiler.Account;
import org.sweetiebelle.mcprofiler.BaseAccount;
import org.sweetiebelle.mcprofiler.CommandSupplement;
import org.sweetiebelle.mcprofiler.Data;
import org.sweetiebelle.mcprofiler.NamesFetcher.Response;
import org.sweetiebelle.mcprofiler.NoDataException;
import org.sweetiebelle.mcprofiler.Settings;
import org.sweetiebelle.mcprofiler.UUIDAlt;

/**
 * This guy does most of the bulk work.
 *
 */
class CommandSupplementBukkit extends CommandSupplement<CommandSender> {
    final static String noPermission = ChatColor.RED + "You do not have permission.";
    private static final Logger logger = LogManager.getLogger();
    final Data d;
    final VanishController vc;
    final Settings s;
    final BansController bc;
    final PermissionsController pc;

    CommandSupplementBukkit(final Settings s, final Data d, final MCProfilerPlugin p) {
        super(s, d);
        this.s = s;
        this.d = d;
        bc = new BansController();
        pc = new PermissionsController();
        final Plugin vnp = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
        if (vnp != null)
            vc = new VanishController(d);
        else
            vc = null;
    }

    @Override
    protected String getPrefix(UUID uuid) {
        return pc.getPrefix(uuid);
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    protected void sendMessage(CommandSender sender, String... message) {
        sender.sendMessage(message);
    }

    /**
     * Worker class for getting an Account from a name or UUID.
     * 
     * @param nameoruuid
     * @param needsLastTime
     * @return
     */
    @Override
    protected Account getAccount(final String nameoruuid, final boolean needsLastTime) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(nameoruuid);
        } catch (final IllegalArgumentException e) {
            uuid = null;
            d.error(e);
        }
        if (uuid == null) {
            for (final Player p : Bukkit.getOnlinePlayers())
                if (p.getName().equalsIgnoreCase(nameoruuid))
                    return d.getAccount(p.getUniqueId(), needsLastTime);
            return d.getAccount(nameoruuid, needsLastTime);
        }
        return d.getAccount(uuid, needsLastTime);
    }

    /**
     * Handle /mcprofiler listlinks
     * 
     * @param a
     * @param altAccounts
     * @param pSender
     * @param recursive
     */
    @Override
    public void displayPossiblePlayerAlts(final Account a, final BaseAccount[] altAccounts, final CommandSender pSender,
            final boolean recursive) {
        boolean first = true;
        if (altAccounts == null)
            throw new NullPointerException("Account list cannot be null!");
        if (recursive) {
            for (final BaseAccount alt : altAccounts) {
                if (first)
                    pSender.sendMessage("§cThe player " + getPrefix(a.getUUID()) + a.getName() + " §c(§f" + a.getIP() + "§c) has the following associated accounts:");
                first = false;
                pSender.sendMessage("§b* " + getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).getName() + " §c(§f" + alt.getIP() + "§c)");
            }
            if (first)
                pSender.sendMessage("§cNo known alts of that Account.");
        } else {
            final ArrayList<UUIDAlt> alreadypassed = new ArrayList<UUIDAlt>(altAccounts.length);
            for (final BaseAccount altAccount : altAccounts) {
                final UUIDAlt alt = (UUIDAlt) altAccount;
                if (alt.getUUID().equals(a.getUUID()) || alreadypassed.contains(alt))
                    continue;
                if (first)
                    pSender.sendMessage("§cThe player " + getPrefix(a.getUUID()) + a.getName() + " §c(§f" + a.getIP() + "§c) has the following associated accounts:");
                first = false;
                pSender.sendMessage("§b* " + getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).getName() + " §c(§f" + alt.getIP() + "§c)");
                alreadypassed.add(alt);
            }
            if (first)
                pSender.sendMessage("§cNo known alts of that Account.");
        }
    }

    /**
     * Gets a timestamp from a unix time.
     * 
     * @param time in seconds
     * @return
     */
    private String getTimeStamp(long time) {
        // SYSTEM TIME IN MILISECONDS
        time = time * 1000L;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating
        return sdf.format(new Date(time));
    }

    /**
     * Display the previous usernames of an Account
     * 
     * @param a
     * @param pSender
     */
    @Override
    public void displayPreviousUsernames(final Account a, final CommandSender pSender) {
        final Response[] previousUsernames = a.getPreviousNames();
        try {
            if (previousUsernames == null)
                throw new NoDataException("Previous Usernames returned null!");
            pSender.sendMessage("§cPlayer " + a.getName() + " has the known previous usernames:");
            for (final Response response : previousUsernames) {
                String time = getTimeStamp(response.changedToAt / 1000L);
                if (response.changedToAt == 0L)
                    time = "Original Name      ";
                pSender.sendMessage("§b " + time + " " + response.name);
            }
        } catch (final NoDataException e) {
            d.error(e);
            pSender.sendMessage("§cAn internal error occured while performing this command.");
        }
    }


    /**
     * Worker class for getting a string from a location
     * 
     * @param location
     * @return
     */
    String getLocation(final Location location) {
        return String.format("%d,%d,%d:%s", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }

    @Override
    public boolean displayPlayerInformation(final String playername, final CommandSender pSender) {
        final Account a = getAccount(playername, true);
        if (hasPermission(pSender, "mcprofiler.info.basic.name")) {
            // Get the UUID for the given player and read the note
            if (a == null) {
                sendMessage(pSender, "§cCould not find the player '§f" + playername + "§c' in the database!");
                return true;
            }
            // Print a "summary" of the given player
            sendMessage(pSender, "§b* " + getPrefix(a.getUUID()) + a.getName());
            if (hasPermission(pSender, "mcprofiler.info.basic.uuid"))
                sendMessage(pSender, "§b* [" + a.getUUID().toString() + "]");
            if (hasPermission(pSender, "mcprofiler.info.basic.previoususernames"))
                displayPreviousUsernames(a, pSender);
        }
        // Check if the player is online or not
        boolean isOnline = false;
        boolean senderCanSee = true;
        Player queriedPlayer = null;
        if (pSender instanceof Player) {
            senderCanSee = false;
            for (final Player player : Bukkit.getServer().getOnlinePlayers())
                if (player.getName().equals(a.getName())) {
                    isOnline = true;
                    if (vc != null)
                        senderCanSee = vc.canSee(player, (Player) pSender);
                    else
                        senderCanSee = true;
                    queriedPlayer = player;
                    break;
                }
        } else {
            senderCanSee = true;
            for (final Player player : Bukkit.getServer().getOnlinePlayers())
                if (player.getName().equals(a.getName())) {
                    isOnline = true;
                    queriedPlayer = player;
                    break;
                }
        }
        if (hasPermission(pSender, "mcprofiler.info.online"))
            if (isOnline && senderCanSee)
                sendMessage(pSender, "§c- §fLast on: §aOnline now");
            else
                sendMessage(pSender, "§c- §fLast on: §9" + a.getLastOn());
        if (hasPermission(pSender, "mcprofiler.info.ip"))
            // Get the player IP address and notes
            sendMessage(pSender, "§c- §fPeer address: §9" + a.getIP());
        // Notes will never be null. Data.java.293 & 232
        if (hasPermission(pSender, "mcprofiler.readnotes"))
            sendMessage(pSender, a.getNotes());
        // Get the position of the player
        if (hasPermission(pSender, "mcprofiler.info.position"))
            if (isOnline && senderCanSee)
                sendMessage(pSender, "§c- §fLocation: §9" + getLocation(queriedPlayer.getLocation()));
            else if (a.getLocation() != null)
                sendMessage(pSender, "§c- §fLocation: §9" + a.getLocation());
            else
                sendMessage(pSender, "§c- §fLocation: §9null");
        return true;
    }

    @Override
    public void notifyStaffOfPossibleAlts(UUID puuid, String name, BaseAccount[] baseAccounts) {
        // Need valid data
        if (d.isNull(name) || baseAccounts == null)
            return;
        // Build the string to display
        String string = getPrefix(puuid) + name + " §fmight be ";
        final ArrayList<UUIDAlt> alreadyadded = new ArrayList<UUIDAlt>(baseAccounts.length);
        for (final BaseAccount altAccount : baseAccounts) {
            final UUIDAlt alt = BaseAccount.getUUIDFromBase(altAccount);
            if (alt == null)
                return;
            final UUID uuid = alt.getUUID();
            if (uuid.equals(puuid) || alreadyadded.contains(alt))
                continue;
            final Account a = d.getAccount(uuid, false);
            if (bc.isBanned(uuid))
                string += getPrefix(a.getUUID()) + a.getName() + " §7(BANNED) §c";
            else
                string += getPrefix(a.getUUID()) + a.getName();
            string += "§f, ";
            alreadyadded.add(alt);
        }
        final String compare = getPrefix(puuid) + name + " §fmight be ";
        logger.debug("Initial String: " + string);
        logger.debug("String to compare: " + compare);
        // Again, their only alt is themselves.
        if (string.equalsIgnoreCase(compare)) {
            logger.debug("Broken!");
            return;
        }
        // Fix the tailing comma
        logger.debug("Valid String: " + string);
        string = string.substring(0, string.length() - 4);
        logger.debug("No comma : " + string);
        // Find all players that should be notified and notify them
        for (final Player admin : Bukkit.getServer().getOnlinePlayers())
            if (admin.hasPermission("mcprofiler.notifiedofalts"))
                admin.sendMessage(string);
    }

}
