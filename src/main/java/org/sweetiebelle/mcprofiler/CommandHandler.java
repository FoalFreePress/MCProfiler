package org.sweetiebelle.mcprofiler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sweetiebelle.mcprofiler.NamesFetcher.Response;
import org.sweetiebelle.mcprofiler.accounts.Account;
import org.sweetiebelle.mcprofiler.accounts.BaseAccount;
import org.sweetiebelle.mcprofiler.accounts.UUIDAlt;

import com.google.common.base.Joiner;

/**
 * This class handles basic commands, but does not really do the bulk of the work.
 *
 */
public class CommandHandler implements CommandExecutor {

    private final static UUID consoleUUID = UUID.fromString("ddc3ad2c-02c6-4c95-ae65-0222a249724f");
    /**
     * The message to send people with no permission.
     */
    private final static String noPermission = "&cYou do not have permission.";
    private BansController bc;
    private Data d;
    private PermissionsController pc;
    private Settings s;
    private VanishController vc;
    public CommandHandler(final Settings set, final Data da, final MCProfilerPlugin p) {
        this.d = da;
        this.s = set;
        bc = new BansController();
        pc = new PermissionsController();
        vc = new VanishController(p, da);
    }

    /**
     * Adds the note to the player.
     * 
     * @param pPlayerName
     * @param pStaffName
     * @param pNote
     * @param pSender
     * @return
     */
    private boolean addNoteToPlayer(final UUID pPlayerUUID, final UUID pStaffUUID, final String pNote, CommandSender pSender) {
        if (hasPermission(pSender, "mcprofiler.addnote")) {
            d.addNoteToUser(pPlayerUUID, pStaffUUID, pNote);
            sendMessage(pSender, "&cAdded the note '&f" + pNote + "&c'" + " to that player&c'!");
            return true;
        }
        sendMessage(pSender, noPermission);
        return true;
    }
    private boolean displayPlayerInformation(final String playername, final CommandSender pSender) {
        final Optional<Account> ao = getAccount(playername, true);
        Account a = null;
        if (hasPermission(pSender, "mcprofiler.info.basic.name")) {
            // Get the UUID for the given player and read the note
            if (!ao.isPresent()) {
                sendMessage(pSender, "&cCould not find the player '&f" + playername + "&c' in the database!");
                return true;
            }
            a = ao.get();
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
            for (final Player player : Bukkit.getServer().getOnlinePlayers())
                if (player.getName().equals(a.getName())) {
                    isOnline = true;
                    senderCanSee = vc.canSee(player, (Player) pSender);
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

    /**
     * Handle /mcprofiler lookup <ip>
     * 
     * @param pIP
     * @param pSender
     * @return
     */
    private boolean displayPlayersLinkedToIP(final String pIP, CommandSender pSender) {
        if (hasPermission(pSender, "mcprofiler.lookup")) {
            // Find the uuids linked to the ip
            final String allUUIDs = d.getUsersAssociatedWithIP(pIP);
            if (d.isNull(allUUIDs)) {
                sendMessage(pSender, "&cDidn't find any accounts linked to the ip '&f" + pIP + "&c'!");
                return true;
            }
            // Get all the uuids and iterate over them
            sendMessage(pSender, "&cThe player accounts linked to the ip '&f" + pIP + "&c' are:");
            final String[] uuids = allUUIDs.split(",");
            try {
                for (final String uuid : uuids)
                    if (!d.isNull(uuid)) {
                        final Optional<Account> ao = getAccount(uuid, false);
                        if (ao.isPresent()) {
                            Account a = ao.get();
                            sendMessage(pSender, "&b* " + getPrefix(a.getUUID()) + a.getName());
                            sendMessage(pSender, "&b* [" + a.getUUID() + "]");
                        } else
                            throw new NoDataException("UUID " + uuid + " did not return an account even though it should have! Enable stackTraces in the config if you haven't already!");
                    } else
                        throw new NoDataException("A UUID did not return an account even though it should have! Enable stackTraces in the config if you haven't already!");
                return true;
            } catch (final NoDataException e) {
                d.error(e);
                sendMessage(pSender, "&cAn internal error occurred while attempting to perform this command.");
            }
        }
        sendMessage(pSender, noPermission);
        return true;
    }

    /**
     * Handle /mcprofiler listlinks
     *
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
                    sendMessage(pSender, "&cThe player " + getPrefix(a.getUUID()) + a.getName() + " &c(&f" + a.getIP() + "&c) has the following associated accounts:");
                first = false;
                sendMessage(pSender, "&b* " + getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).get().getName() + " &c(&f" + alt.getIP() + "&c)");
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
                sendMessage(pSender, "&b* " + getPrefix(alt.getUUID()) + d.getAccount(alt.getUUID(), false).get().getName() + " &c(&f" + alt.getIP() + "&c)");
                alreadypassed.add(alt);
            }
            if (first)
                sendMessage(pSender, "&cNo known alts of that Account.");
        }
    }

    /**
     * Display the previous usernames of an Account
     *
     * @param a
     * @param pSender
     */
    private void displayPreviousUsernames(final Account a, final CommandSender pSender) {
        final Response[] previousUsernames = a.getPreviousNames();
        try {
            if (previousUsernames == null)
                throw new NoDataException("Previous Usernames returned null!");
            sendMessage(pSender, "&cPlayer " + a.getName() + " has the known previous usernames:");
            for (final Response response : previousUsernames) {
                String time = getTimeStamp(response.changedToAt / 1000L);
                if (response.changedToAt == 0L)
                    time = "Original Name      ";
                sendMessage(pSender, "&b " + time + " " + response.name);
            }
        } catch (final NoDataException e) {
            d.error(e);
            sendMessage(pSender, "&cAn internal error occured while performing this command.");
        }
    }

    /**
     * handle /mcprofiler listlinks
     * 
     * @param pPlayerName
     * @param pSender
     * @param recursive
     * @return
     */
    private boolean findLinkedUsers(final String pPlayerName, CommandSender pSender, final boolean recursive) {
        if (hasPermission(pSender, "mcprofiler.listlinks")) {
            final Optional<Account> ao = getAccount(pPlayerName, false);
            if (ao.isPresent()) {
                Account a = ao.get();
                displayPossiblePlayerAlts(a, d.getAltsOfPlayer(a.getUUID(), recursive), pSender, recursive);
                return true;
            }
            sendMessage(pSender, "&cCouldn't find the player '&f" + pPlayerName + "&c' in the database!");
            return true;
        }
        sendMessage(pSender, noPermission);
        return true;
    }

    /**
     * Worker class for getting an Account from a name or UUID.
     *
     * @param nameoruuid
     * @param needsLastTime
     * @return
     */
    private Optional<Account> getAccount(final String nameoruuid, final boolean needsLastTime) {
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
     * 
     * @param name
     * @param pSender
     * @return
     */
    private boolean getIPsByPlayer(final String name, CommandSender pSender) {
        final Optional<Account> ao = getAccount(name, false);
        if (!ao.isPresent()) {
            sendMessage(pSender, "&cCould not find the player '&f" + name + "&c' in the database!");
            return true;
        }
        Account a = ao.get();
        if (hasPermission(pSender, "mcprofiler.listips")) {
            // Find the ips linked to the playernmae
            final String foundIPs = d.getIPsByPlayer(a);
            // Get all the uuids and iterate over them
            sendMessage(pSender, "&cThe ips linked to the player '&f" + a.getName() + "&c' are:");
            final String[] ips = foundIPs.split(",");
            for (final String ip : ips)
                sendMessage(pSender, "&b* " + ip);
            return true;
        }
        sendMessage(pSender, noPermission);
        return true;
    }

    /**
     * Worker class for getting a string from a location
     *
     * @param location
     * @return
     */
    private String getLocation(final Location location) {
        return String.format("%d,%d,%d:%s", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
    }

    /**
     * Gets a playername from a UUID.
     * 
     * @param pUUIDString
     * @param pSender
     * @return
     */
    private boolean getPlayerFromUUID(final String pUUIDString, CommandSender pSender) {
        if (hasPermission(pSender, "mcprofiler.uuid")) {
            Optional<Account> ao = null;
            Account a = null;
            try {
                ao = d.getAccount(UUID.fromString(pUUIDString), false);
            } catch (final IllegalArgumentException e) {
                d.error(e);
                sendMessage(pSender, "&cThat is not a UUID.");
                return true;
            }
            if (ao.isPresent()) {
                a = ao.get();
                sendMessage(pSender, "&b* " + getPrefix(a.getUUID()) + a.getName());
                sendMessage(pSender, "&b* [" + a.getUUID().toString() + "]");
                return true;
            }
            sendMessage(pSender, "&cThat is not a UUID.");
            return true;
        }
        sendMessage(pSender, noPermission);
        return true;
    }

    private String getPrefix(final UUID uuid) {
        return pc.getPrefix(uuid);
    }

    /**
     * Gets a timestamp from a unix time.
     *
     * @param time
     *            in seconds
     * @return
     */
    private String getTimeStamp(long time) {
        // SYSTEM TIME IN MILISECONDS
        time = time * 1000L;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating
        return sdf.format(new Date(time));
    }

    private boolean hasPermission(final CommandSender sender, final String permission) {
        return sender.hasPermission(permission);
    }

    /**
     * Handle /mcprofiler maintenance
     * 
     * @param args
     * @param sender
     * @return
     */
    private boolean maintenance(final String[] args, CommandSender sender) {
        if (hasPermission(sender, "mcprofiler.maintenance")) {
            if (args.length == 1) {
                // They did /MCProfiler args[0].equals("maintenance")
                sendMessage(sender, "&c/MCProfiler maintenance fixnotes <UUID> <name>  &f - Associates a playername with the UUID.");
                sendMessage(sender, "&c/MCProfiler maintenance forcemakeaccount UUID lastKnownName IP &f - Forces an account to be made for /MCProfiler info. If you don't know the IP, type in NULL.");
                sendMessage(sender, "&c/MCProfiler maintenance updatename UUID newname &f - Forces an account to be updated with the new name.");
                sendMessage(sender, "&cIf &f-1&c rows are affected, then there was an error performing the query.");
                return true;
            }
            if (args[1].equalsIgnoreCase("fixnotes") && args.length == 4) {
                // MCProfiler maintenance fixnotes UUID name
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[2]);
                } catch (final IllegalArgumentException e) {
                    // Not a UUID
                    sendMessage(sender, "&cThat is not a UUID.");
                    d.error(e);
                    return true;
                }
                sendMessage(sender, "&aQuery Okay, " + d.maintenance("UPDATE " + s.dbPrefix + "notes SET UUID = \"" + uuid.toString() + "\" where lastKnownName = \"" + args[3] + "\";") + " rows affected.");
                return true;
            }
            if (args[1].equalsIgnoreCase("forcemakeaccount") && args.length == 5) {
                // MCProfiler maintenance forcemakeaccount UUID lastKnownName IP
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[2]);
                } catch (final IllegalArgumentException e) {
                    // Not a UUID
                    sendMessage(sender, "&cThat is not a UUID.");
                    d.error(e);
                    return true;
                }
                sendMessage(sender, "&aQuery Okay, " + d.maintenance("INSERT INTO " + s.dbPrefix + "profiles (uuid, lastKnownName, ip) VALUES (\"" + uuid.toString() + "\", \"" + args[3] + "\", \"" + args[4] + "\");") + " rows affected.");
                return true;
            }
            if (args[1].equalsIgnoreCase("updatename") && args.length == 4) {
                // MCProfiler maintenance updatename UUID newname
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[2]);
                } catch (final IllegalArgumentException e) {
                    // Not a UUID
                    sendMessage(sender, "&cThat is not a UUID.");
                    d.error(e);
                    return true;
                }
                sendMessage(sender, "&aQuery Okay, " + d.maintenance("UPDATE " + s.dbPrefix + "profiles SET lastKnownName = \"" + args[3] + "\" where uuid = \"" + uuid.toString() + "\";") + " rows affected.");
                return true;
            }
            sendMessage(sender, "&c/MCProfiler maintenance fixnotes <UUID> <name>  &f - Associates a playername with the UUID.");
            sendMessage(sender, "&c/MCProfiler maintenance forcemakeaccount UUID lastKnownName IP &f - Forces an account to be made for /MCProfiler info. If you don't know the IP, type in NULL.");
            sendMessage(sender, "&c/MCProfiler maintenance updatename UUID newname &f - Forces an account to be updated with the new name.");
            sendMessage(sender, "&cIf &f-1&c rows are affected, then there was an error performing the query.");
            return true;
        }
        sendMessage(sender, noPermission);
        return true;
    }

    void notifyStaffOfPossibleAlts(final UUID puuid, final String name, final BaseAccount[] baseAccounts) {
        // Need valid data
        if (d.isNull(name) || baseAccounts == null)
            return;
        // Build the string to display
        String string = getPrefix(puuid) + name + " &fmight be ";
        final ArrayList<UUIDAlt> alreadyadded = new ArrayList<UUIDAlt>(baseAccounts.length);
        for (final BaseAccount altAccount : baseAccounts) {
            final UUIDAlt alt = new UUIDAlt(altAccount.getUUID(), altAccount.getIP());
            final UUID uuid = alt.getUUID();
            if (uuid.equals(puuid) || alreadyadded.contains(alt))
                continue;
            final Account a = d.getAccount(uuid, false).get();
            if (bc.isBanned(uuid))
                string += getPrefix(a.getUUID()) + a.getName() + " &7(BANNED) &c";
            else
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
        string = string.replace('&', ChatColor.COLOR_CHAR);
        // Find all players that should be notified and notify them
        for (final Player admin : Bukkit.getServer().getOnlinePlayers())
            if (admin.hasPermission("mcprofiler.notifiedofalts"))
                admin.sendMessage(string);
        Bukkit.getConsoleSender().sendMessage(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        // Handle short commands.
        if (command.getName().equalsIgnoreCase("status")) {
            if (args.length == 1)
                return this.displayPlayerInformation(args[0], sender);
            return false;
        }
        if (command.getName().equalsIgnoreCase("note")) {
            if (args.length >= 2) {
                if (sender.hasPermission("mcprofiler.addnote")) {
                    final String note = Joiner.on(' ').join(ArrayUtils.subarray(args, 1, args.length));
                    Optional<Account> ao = this.getAccount(args[0], false);
                    if (ao.isPresent()) {
                        if (sender instanceof Player)
                            return this.addNoteToPlayer(ao.get().getUUID(), ((Player) sender).getUniqueId(), note, sender);
                        return this.addNoteToPlayer(ao.get().getUUID(), this.consoleUUID, note, sender);
                    }
                    this.sendMessage(sender, "&cCould not find the player '&f" + args[0] + "&c' in the database!");
                    return true;
                }
                sender.sendMessage(noPermission);
                return true;
            }
            return false;
        }
        // Make sure there's enough arguments, if not, end here
        if (args.length == 0) {
            sender.sendMessage("Not enough arguments. Type /mcprofiler help for help.");
            return true;
        }
        // Figure out what command to execute
        final String instruction = args[0];
        boolean recursive = false;
        if (instruction.equals("help")) {
            if (sender.hasPermission("mcprofiler.help")) {
                this.sendMessage(sender, "&bMCProfiler help menu:");
                this.sendMessage(sender, "&c/MCProfiler addnote <playerName> <note> &f - Adds a note to the given player");
                this.sendMessage(sender, "&c/MCProfiler readnotes <playerName> &f - Displays the notes on the given player");
                this.sendMessage(sender, "&c/MCProfiler info <playerName|uuid> &f - Displays a summary of the player");
                this.sendMessage(sender, "&c/status <playername|uuid> &f - short for /MCProfiler info");
                this.sendMessage(sender, "&c/MCProfiler lookup <ip> &f - Displays all accounts linked to the given IP");
                this.sendMessage(sender, "&c/MCProfiler listlinks [-r] <playerName> &f - Displays all accounts that might be linked to the given user. Use the -r flag for recursive player searching. This displays all of the alts of the player's alts, and all the alts of those alts....");
                this.sendMessage(sender, "&c/MCProfiler listips <playerName> &f - Lists all known IPs from a given player");
                this.sendMessage(sender, "&c/MCProfiler uuid <uuid> &f - Displays a username based on a UUID.");
                this.sendMessage(sender, "&c/MCProfiler maintenance <fixnotes | forcemakeaccount | updatename> <args> &f - Performs maintence commands See /MCProfiler maintenance with no args for help.");
                this.sendMessage(sender, "&c/MCProfiler reload&f - Reloads general configuration settings.");
                return true;
            }
            sender.sendMessage(noPermission);
            return true;
        }
        if (instruction.equals("addnote") && args.length >= 3) {
            if (sender.hasPermission("mcprofiler.addnote")) {
                final String note = Joiner.on(' ').join(ArrayUtils.subarray(args, 2, args.length));
                Optional<Account> ao = this.getAccount(args[1], false);
                if (ao.isPresent()) {
                    if (sender instanceof Player)
                        return this.addNoteToPlayer(ao.get().getUUID(), ((Player) sender).getUniqueId(), note, sender);
                    return this.addNoteToPlayer(ao.get().getUUID(), this.consoleUUID, note, sender);
                }
                this.sendMessage(sender, "&cCould not find the player '&f" + args[1] + "&c' in the database!");
                return true;
            }
            sender.sendMessage(noPermission);
            return true;
        }
        if (args.length >= 2)
            if (args[1].equalsIgnoreCase("-r"))
                recursive = true;
        if (instruction.equalsIgnoreCase("lookup") && args.length == 2)
            return this.displayPlayersLinkedToIP(args[1], sender);
        if (instruction.equalsIgnoreCase("readnotes") && args.length == 2)
            return this.readNoteForPlayer(args[1], sender);
        if (instruction.equalsIgnoreCase("info") && args.length == 2)
            return this.displayPlayerInformation(args[1], sender);
        if (instruction.equalsIgnoreCase("listlinks") && (args.length == 2 || args.length == 3))
            return this.findLinkedUsers(recursive ? args[2] : args[1], sender, recursive);
        if (instruction.equalsIgnoreCase("uuid") && args.length == 2)
            return this.getPlayerFromUUID(args[1], sender);
        if (instruction.equalsIgnoreCase("reload") && args.length == 1)
            return this.reloadSettings(sender);
        if (instruction.equalsIgnoreCase("listips") && args.length == 2)
            return this.getIPsByPlayer(args[1], sender);
        if (instruction.equalsIgnoreCase("maintenance"))
            return this.maintenance(args, sender);
        sender.sendMessage("&cInvalid instruction '" + instruction + "' or invalid number of parameters. Try to use /MCProfiler help");
        return true;
    }

    /**
     * Read the notes of the player.
     * 
     * @param pPlayerName
     *            The player's name
     * @param pSender
     * @return
     */
    private boolean readNoteForPlayer(final String pPlayerName, CommandSender pSender) {
        if (hasPermission(pSender, "mcprofiler.readnotes")) {
            final Optional<Account> ao = getAccount(pPlayerName, false);
            // Get the UUID for the given player and read the note
            if (ao.isPresent())
                sendMessage(pSender, ao.get().getNotes());
            else
                sendMessage(pSender, "&cCouldn't find the player '&f" + pPlayerName + "&c' in the database!");
            return true;
        }
        sendMessage(pSender, noPermission);
        return true;
    }

    /**
     * Handle /MCProfiler reload
     * 
     * @param sender
     * @return
     */
    private boolean reloadSettings(CommandSender sender) {
        if (hasPermission(sender, "mcprofiler.reload")) {
            sendMessage(sender, "&cReloading plugin...");
            s.reloadSettings();
            d.forceConnectionRefresh();
            return true;
        }
        sendMessage(sender, noPermission);
        return true;
    }

    private void sendMessage(final CommandSender sender, final String... message) {
        for (String msg : message)
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
