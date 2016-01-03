package org.sweetiebelle.mcprofiler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.sweetiebelle.mcprofiler.NamesFetcher.Response;

/**
 * This guy does most of the bulk work.
 *
 */
class CommandSupplement {
    private final static String noPermission = ChatColor.RED + "You do not have permission.";
    private final Data d;
    private final VanishController vc;
    private final Settings s;
    private final BansController bc;
    private final PermissionsController pc;

    CommandSupplement(final Settings s, final Data d, final MCProfilerPlugin p) {
        this.s = s;
        this.d = d;
        bc = new BansController(p);
        pc = new PermissionsController(p);
        final Plugin vnp = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
        if (vnp != null)
            vc = new VanishController(d);
        else
            vc = null;
    }

    /**
     * Adds the note to the player.
     * @param pPlayerName
     * @param pStaffName
     * @param pNote
     * @param pSender
     * @return
     */
    boolean addNoteToPlayer(final String pPlayerName, final String pStaffName, final String pNote, final CommandSender pSender) {
        if (pSender.hasPermission("mcprofiler.addnote")) {
            final Account a = getAccount(pPlayerName, false);
            if (a != null) {
                d.addNoteToUser(a.getUUID(), pStaffName, pNote);
                pSender.sendMessage("§cAdded the note '§f" + pNote + "§c'" + " to player '§f" + a.getName() + "§c'!");
                return true;
            }
            pSender.sendMessage("§cCould not find the player '§f" + pPlayerName + "§c' in the database!");
            return true;
        }
        pSender.sendMessage(noPermission);
        return true;
    }

    /**
     * Called on a /MCProfiler info
     * @param playername
     * @param pSender
     * @return
     */
    boolean displayPlayerInformation(final String playername, final CommandSender pSender) {
        final Account a = getAccount(playername, true);
        if (pSender.hasPermission("mcprofiler.info.basic.name")) {
            // Get the UUID for the given player and read the note
            if (a == null) {
                pSender.sendMessage("§cCould not find the player '§f" + playername + "§c' in the database!");
                return true;
            }
            // Print a "summary" of the given player
            pSender.sendMessage("§b* " + pc.getPrefix(a.getUUID()) + a.getName());
            if (pSender.hasPermission("mcprofiler.info.basic.uuid"))
                pSender.sendMessage("§b* [" + a.getUUID().toString() + "]");
            if (pSender.hasPermission("mcprofiler.info.basic.previoususernames"))
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
        if (pSender.hasPermission("mcprofiler.info.online"))
            if (isOnline && senderCanSee)
                pSender.sendMessage("§c- §fLast on: §aOnline now");
            else
                pSender.sendMessage("§c- §fLast on: §9" + a.getLastOn());
        if (pSender.hasPermission("mcprofiler.info.ip"))
            // Get the player IP address and notes
            pSender.sendMessage("§c- §fPeer address: §9" + a.getIP());
        // Notes will never be null. Data.java.293 & 232
        if (pSender.hasPermission("mcprofiler.readnotes"))
            pSender.sendMessage(a.getNotes());
        // Get the position of the player
        if (pSender.hasPermission("mcprofiler.info.position"))
            if (isOnline && senderCanSee)
                pSender.sendMessage("§c- §fLocation: §9" + getLocation(queriedPlayer.getLocation()));
            else if (a.getLocation() != null)
                pSender.sendMessage("§c- §fLocation: §9" + a.getLocation());
            else
                pSender.sendMessage("§c- §fLocation: §9null");
        return true;
    }

    /**
     * Handle /mcprofiler lookup <ip>
     * @param pIP
     * @param pSender
     * @return
     */
    boolean displayPlayersLinkedToIP(final String pIP, final CommandSender pSender) {
        if (pSender.hasPermission("mcprofiler.lookup")) {
            // Find the uuids linked to the ip
            final String allUUIDs = d.getUsersAssociatedWithIP(pIP);
            if (d.isNull(allUUIDs)) {
                pSender.sendMessage("§cDidn't find any accounts linked to the ip '§f" + pIP + "§c'!");
                return true;
            }
            // Get all the uuids and iterate over them
            pSender.sendMessage("§cThe player accounts linked to the ip '§f" + pIP + "§c' are:");
            final String[] uuids = allUUIDs.split(",");
            try {
                for (final String uuid : uuids)
                    if (!d.isNull(uuid)) {
                        final Account a = getAccount(uuid, false);
                        if (a != null) {
                            pSender.sendMessage("§b* " + pc.getPrefix(a.getUUID()) + a.getName());
                            pSender.sendMessage("§b* [" + a.getUUID() + "]");
                        } else
                            throw new NoDataException("UUID " + uuid + " did not return an account even though it should have! Enable stackTraces in the config if you haven't already!");
                    } else
                        throw new NoDataException("A UUID did not return an account even though it should have! Enable stackTraces in the config if you haven't already!");
                return true;
            } catch (final NoDataException e) {
                d.error(e);
                pSender.sendMessage("§cAn internal error occurred while attempting to perform this command.");
            }
        }
        pSender.sendMessage(noPermission);
        return true;
    }

    /**
     * Handle /mcprofiler listlinks
     * @param a
     * @param altAccounts
     * @param pSender
     * @param recursive
     */
    private void displayPossiblePlayerAlts(final Account a, final BaseAccount[] altAccounts, final CommandSender pSender, final boolean recursive) {
        boolean first = true;
        if (altAccounts == null)
            throw new NullPointerException("Account list cannot be null!");
        if (recursive) {
            for (final BaseAccount alt : altAccounts) {
                if (first)
                    pSender.sendMessage("§cThe player " + pc.getPrefix(a.getUUID()) + a.getName() + " §c(§f" + a.getIP() + "§c) has the following associated accounts:");
                first = false;
                pSender.sendMessage("§b* " + pc.getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).getName() + " §c(§f" + alt.getIP() + "§c)");
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
                    pSender.sendMessage("§cThe player " + pc.getPrefix(a.getUUID()) + a.getName() + " §c(§f" + a.getIP() + "§c) has the following associated accounts:");
                first = false;
                pSender.sendMessage("§b* " + pc.getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).getName() + " §c(§f" + alt.getIP() + "§c)");
                alreadypassed.add(alt);
            }
            if (first)
                pSender.sendMessage("§cNo known alts of that Account.");
        }
    }

    /**
     * Gets a timestamp from a unix time.
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
     * @param a
     * @param pSender
     */
    private void displayPreviousUsernames(final Account a, final CommandSender pSender) {
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
     * handle /mcprofiler listlinks
     * @param pPlayerName
     * @param pSender
     * @param recursive
     * @return
     */
    boolean findLinkedUsers(final String pPlayerName, final CommandSender pSender, final boolean recursive) {
        if (pSender.hasPermission("mcprofiler.listlinks")) {
            final Account a = getAccount(pPlayerName, false);
            if (a != null) {
                displayPossiblePlayerAlts(a, d.getAltsOfPlayer(a.getUUID(), recursive), pSender, recursive);
                return true;
            }
            pSender.sendMessage("§cCouldn't find the player '§f" + pPlayerName + "§c' in the database!");
            return true;
        }
        pSender.sendMessage(noPermission);
        return true;
    }

    /**
     * Worker class for getting an Account from a name or UUID.
     * @param nameoruuid
     * @param needsLastTime
     * @return
     */
    private Account getAccount(final String nameoruuid, final boolean needsLastTime) {
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
     * handle /MCProfiler listips
     * @param name
     * @param pSender
     * @return
     */
    boolean getIPsByPlayer(final String name, final CommandSender pSender) {
        final Account a = getAccount(name, false);
        if (a == null) {
            pSender.sendMessage("§cCould not find the player '§f" + name + "§c' in the database!");
            return true;
        }
        if (pSender.hasPermission("mcprofiler.listips")) {
            // Find the ips linked to the playernmae
            final String foundIPs = d.getIPsByPlayer(a);
            // Get all the uuids and iterate over them
            pSender.sendMessage("§cThe ips linked to the player '§f" + a.getName() + "§c' are:");
            final String[] ips = foundIPs.split(",");
            for (final String ip : ips)
                pSender.sendMessage("§b* " + ip);
            return true;
        }
        pSender.sendMessage(noPermission);
        return true;
    }

    /**
     * Worker class for getting a string from a location
     * @param location
     * @return
     */
    private String getLocation(final Location location) {
        return new String(Integer.toString(location.getBlockX()) + "," + Integer.toString(location.getBlockY()) + "," + Integer.toString(location.getBlockZ()) + ":" + location.getWorld().getName());
    }

    /**
     * Gets a playername from a UUID.
     * @param pUUIDString
     * @param pSender
     * @return
     */
    boolean getPlayerFromUUID(final String pUUIDString, final CommandSender pSender) {
        if (pSender.hasPermission("mcprofiler.uuid")) {
            Account a = null;
            try {
                a = d.getAccount(UUID.fromString(pUUIDString), false);
            } catch (final IllegalArgumentException e) {
                d.error(e);
                pSender.sendMessage("§cThat is not a UUID.");
                return true;
            }
            if (a != null) {
                pSender.sendMessage("§b* " + pc.getPrefix(a.getUUID()) + a.getName());
                pSender.sendMessage("§b* [" + a.getUUID().toString() + "]");
                return true;
            }
            pSender.sendMessage("§cThat is not a UUID.");
            return true;
        }
        pSender.sendMessage(noPermission);
        return true;
    }

    /**
     * Handle /mcprofiler maintenance
     * @param args
     * @param sender
     * @return
     */
    boolean maintenance(final String[] args, final CommandSender sender) {
        if (sender.hasPermission("mcprofiler.maintenance")) {
            if (args.length == 1) {
                // They did /MCProfiler args[0].equals("maintenance")
                sender.sendMessage("§c/MCProfiler maintenance fixnotes <UUID> <name>  §f - Associates a playername with the UUID.");
                sender.sendMessage("§c/MCProfiler maintenance forcemakeaccount UUID lastKnownName IP §f - Forces an account to be made for /MCProfiler info. If you don't know the IP, type in NULL.");
                sender.sendMessage("§c/MCProfiler maintenance updatename UUID newname §f - Forces an account to be updated with the new name.");
                sender.sendMessage("§cIf §f-1§c rows are affected, then there was an error performing the query.");
                return true;
            }
            if (args[1].equalsIgnoreCase("fixnotes") && args.length == 4) {
                // MCProfiler maintenance fixnotes UUID name
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[2]);
                } catch (final IllegalArgumentException e) {
                    // Not a UUID
                    sender.sendMessage("§cThat is not a UUID.");
                    d.error(e);
                    return true;
                }
                sender.sendMessage("§aQuery Okay, " + d.maintenance("UPDATE " + s.dbPrefix + "notes SET UUID = \"" + uuid.toString() + "\" where lastKnownName = \"" + args[3] + "\";") + " rows affected.");
                return true;
            }
            if (args[1].equalsIgnoreCase("forcemakeaccount") && args.length == 5) {
                // MCProfiler maintenance forcemakeaccount UUID lastKnownName IP
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[2]);
                } catch (final IllegalArgumentException e) {
                    // Not a UUID
                    sender.sendMessage("§cThat is not a UUID.");
                    d.error(e);
                    return true;
                }
                sender.sendMessage("§aQuery Okay, " + d.maintenance("INSERT INTO " + s.dbPrefix + "profiles (uuid, lastKnownName, ip) VALUES (\"" + uuid.toString() + "\", \"" + args[3] + "\", \"" + args[4] + "\");") + " rows affected.");
                return true;
            }
            if (args[1].equalsIgnoreCase("updatename") && args.length == 4) {
                // MCProfiler maintenance updatename UUID newname
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[2]);
                } catch (final IllegalArgumentException e) {
                    // Not a UUID
                    sender.sendMessage("§cThat is not a UUID.");
                    d.error(e);
                    return true;
                }
                sender.sendMessage("§aQuery Okay, " + d.maintenance("UPDATE " + s.dbPrefix + "profiles SET lastKnownName = \"" + args[3] + "\" where uuid = \"" + uuid.toString() + "\";") + " rows affected.");
                return true;
            }
            sender.sendMessage("§c/MCProfiler maintenance fixnotes <UUID> <name>  §f - Associates a playername with the UUID.");
            sender.sendMessage("§c/MCProfiler maintenance forcemakeaccount UUID lastKnownName IP §f - Forces an account to be made for /MCProfiler info. If you don't know the IP, type in NULL.");
            sender.sendMessage("§c/MCProfiler maintenance updatename UUID newname §f - Forces an account to be updated with the new name.");
            sender.sendMessage("§cIf §f-1§c rows are affected, then there was an error performing the query.");
            return true;
        }
        sender.sendMessage(noPermission);
        return true;
    }

    /**
     * Handle the displaying of alts to people with a permission node.
     * @param player the player
     * @param altAccounts their alts
     */
    void notifyStaffOfPossibleAlts(final Player player, final BaseAccount[] baseAccounts) {
        // Need valid data
        if (d.isNull(player.getName()) || baseAccounts == null)
            return;
        // Build the string to display
        String string = pc.getPrefix(player.getUniqueId()) + player.getName() + " §fmight be ";
        final ArrayList<UUIDAlt> alreadyadded = new ArrayList<UUIDAlt>(baseAccounts.length);
        for (final BaseAccount altAccount : baseAccounts) {
            final UUIDAlt alt = BaseAccount.getUUIDFromBase(altAccount);
            if (alt == null)
                return;
            final UUID uuid = alt.getUUID();
            if (uuid.equals(player.getUniqueId()) || alreadyadded.contains(alt))
                continue;
            final Account a = d.getAccount(uuid, false);
            if (bc.isBanned(uuid))
                string += pc.getPrefix(a.getUUID()) + a.getName() + " §7(BANNED) §c";
            else
                string += pc.getPrefix(a.getUUID()) + a.getName();
            string += "§f, ";
            alreadyadded.add(alt);
        }
        final String compare = pc.getPrefix(player.getUniqueId()) + player.getName() + " §fmight be ";
        MCProfilerPlugin.debug("Initial String: " + string);
        MCProfilerPlugin.debug("String to compare: " + compare);
        // Again, their only alt is themselves.
        if (string.equalsIgnoreCase(compare)) {
            MCProfilerPlugin.debug("Broken!");
            return;
        }
        // Fix the tailing comma
        MCProfilerPlugin.debug("Valid String: " + string);
        string = string.substring(0, string.length() - 4);
        MCProfilerPlugin.debug("No comma : " + string);
        // Find all players that should be notified and notify them
        for (final Player admin : Bukkit.getServer().getOnlinePlayers())
            if (admin.hasPermission("mcprofiler.notifiedofalts"))
                admin.sendMessage(string);
    }

    /**
     * Read the notes of the player.
     * @param pPlayerName The player's name
     * @param pSender
     * @return
     */
    boolean readNoteForPlayer(final String pPlayerName, final CommandSender pSender) {
        if (pSender.hasPermission("mcprofiler.readnotes")) {
            final Account a = getAccount(pPlayerName, false);
            // Get the UUID for the given player and read the note
            if (a != null)
                pSender.sendMessage(a.getNotes());
            else
                pSender.sendMessage("§cCouldn't find the player '§f" + pPlayerName + "§c' in the database!");
            return true;
        }
        pSender.sendMessage(noPermission);
        return true;
    }

    /**
     * Handle /MCProfiler reload
     * @param sender
     * @return
     */
    boolean reloadSettings(final CommandSender sender) {
        if (sender.hasPermission("mcprofiler.reload")) {
            sender.sendMessage("§cReloading plugin...");
            s.reloadSettings();
            d.forceConnectionRefresh();
            return true;
        }
        sender.sendMessage(noPermission);
        return true;
    }
}
