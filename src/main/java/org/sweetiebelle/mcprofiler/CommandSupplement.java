package org.sweetiebelle.mcprofiler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public abstract class CommandSupplement<Sender> {
    private final static String noPermission = "&cYou do not have permission.";
    protected final Data d;
    protected final Settings s;

    public CommandSupplement(final Settings settings, final Data data) {
        this.s = settings;
        this.d = data;
    }

    protected abstract Account getAccount(String name, boolean needsLastTime);

    protected abstract boolean hasPermission(Sender sender, String permission);

    protected abstract void sendMessage(Sender sender, String... message);

    protected abstract String getPrefix(UUID uuid);

    /**
     * Called on a /MCProfiler info
     * @param playername
     * @param pSender
     * @return
     */
    public abstract boolean displayPlayerInformation(final String playername, final Sender pSender);

    public abstract void displayPreviousUsernames(Account acount, Sender sender);

    public abstract void displayPossiblePlayerAlts(Account a, BaseAccount[] altAccounts, Sender pSender, boolean recursive);

    /**
     * Handle the displaying of alts to people with a permission node.
     * @param player the player
     * @param altAccounts their alts
     */
    public abstract void notifyStaffOfPossibleAlts(UUID uuid, String name, final BaseAccount[] baseAccounts);

    /**
     * Adds the note to the player.
     * @param pPlayerName
     * @param pStaffName
     * @param pNote
     * @param pSender
     * @return
     */
    public boolean addNoteToPlayer(final String pPlayerName, final String pStaffName, final String pNote, final Sender pSender) {
        if (hasPermission(pSender, "mcprofiler.addnote")) {
            final Account a = getAccount(pPlayerName, false);
            if (a != null) {
                d.addNoteToUser(a.getUUID(), pStaffName, pNote);
                sendMessage(pSender, "&cAdded the note '&f" + pNote + "&c'" + " to player '&f" + a.getName() + "&c'!");
                return true;
            }
            sendMessage(pSender, "&cCould not find the player '&f" + pPlayerName + "&c' in the database!");
            return true;
        }
        sendMessage(pSender, noPermission);
        return true;
    }

    /**
     * Handle /mcprofiler lookup <ip>
     * @param pIP
     * @param pSender
     * @return
     */
    public boolean displayPlayersLinkedToIP(final String pIP, final Sender pSender) {
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
                        final Account a = getAccount(uuid, false);
                        if (a != null) {
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
     * Gets a timestamp from a unix time.
     *
     * @param time in seconds
     * @return
     */
    protected String getTimeStamp(long time) {
        // SYSTEM TIME IN MILISECONDS
        time = time * 1000L;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating
        return sdf.format(new Date(time));
    }

    /**
     * handle /mcprofiler listlinks
     * @param pPlayerName
     * @param pSender
     * @param recursive
     * @return
     */
    public boolean findLinkedUsers(final String pPlayerName, final Sender pSender, final boolean recursive) {
        if (hasPermission(pSender, "mcprofiler.listlinks")) {
            final Account a = getAccount(pPlayerName, false);
            if (a != null) {
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
     * handle /MCProfiler listips
     * @param name
     * @param pSender
     * @return
     */
    public boolean getIPsByPlayer(final String name, final Sender pSender) {
        final Account a = getAccount(name, false);
        if (a == null) {
            sendMessage(pSender, "&cCould not find the player '&f" + name + "&c' in the database!");
            return true;
        }
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
     * Gets a playername from a UUID.
     * @param pUUIDString
     * @param pSender
     * @return
     */
    public boolean getPlayerFromUUID(final String pUUIDString, final Sender pSender) {
        if (hasPermission(pSender, "mcprofiler.uuid")) {
            Account a = null;
            try {
                a = d.getAccount(UUID.fromString(pUUIDString), false);
            } catch (final IllegalArgumentException e) {
                d.error(e);
                sendMessage(pSender, "&cThat is not a UUID.");
                return true;
            }
            if (a != null) {
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

    /**
     * Handle /mcprofiler maintenance
     * @param args
     * @param sender
     * @return
     */
    public boolean maintenance(final String[] args, final Sender sender) {
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

    /**
     * Read the notes of the player.
     * @param pPlayerName The player's name
     * @param pSender
     * @return
     */
    public boolean readNoteForPlayer(final String pPlayerName, final Sender pSender) {
        if (hasPermission(pSender, "mcprofiler.readnotes")) {
            final Account a = getAccount(pPlayerName, false);
            // Get the UUID for the given player and read the note
            if (a != null)
                sendMessage(pSender, a.getNotes());
            else
                sendMessage(pSender, "&cCouldn't find the player '&f" + pPlayerName + "&c' in the database!");
            return true;
        }
        sendMessage(pSender, noPermission);
        return true;
    }

    /**
     * Handle /MCProfiler reload
     * @param sender
     * @return
     */
    public boolean reloadSettings(final Sender sender) {
        if (hasPermission(sender, "mcprofiler.reload")) {
            sendMessage(sender, "&cReloading plugin...");
            s.reloadSettings();
            d.forceConnectionRefresh();
            return true;
        }
        sendMessage(sender, noPermission);
        return true;
    }
}