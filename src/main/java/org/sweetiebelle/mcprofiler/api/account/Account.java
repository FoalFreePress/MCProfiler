package org.sweetiebelle.mcprofiler.api.account;

import java.util.Objects;
import java.util.UUID;

import org.sweetiebelle.mcprofiler.NamesFetcher.Response;

/**
 * An Account contains a player's UUID, their name, their last online time, their last Location, their last IP and their notes.
 *
 */
public class Account {

    /**
     * The player's last IP.
     */
    private final String ip;
    /**
     * The player's last online time.
     */
    private final String laston;
    /**
     * The player's last location.
     */
    private final String location;
    /**
     * The player's last known name.
     */
    private final String name;
    /**
     * The player's previous usernames. Key is the name, String is when they changed at.
     */
    private final Response[] names;
    /**
     * The player's notes.
     */
    private final String[] notes;
    /**
     * The UUID of the player.
     */
    private final UUID uuid;
    
    /**
     * Does this account exist in the database?
     */
    private final boolean exists;

    /**
     * Constructs a new Account.
     *
     * @param uuid
     * @param name
     * @param laston
     * @param location
     * @param ip
     * @param notes
     * @param names
     */
    public Account(final UUID uuid, final String name, final String laston, final String location, final String ip, final String[] notes, final Response[] names, final boolean exists) {
        this.uuid = Objects.requireNonNull(uuid);
        this.name = Objects.requireNonNull(name);
        this.laston = laston == null ? "null" : laston;
        this.location = location;
        this.ip = ip == null ? "null" : ip;
        this.notes = Objects.requireNonNull(notes);
        this.names = names;
        this.exists = exists;
    }

    /**
     * If this Account's uuid equals the argument's {@link UUID}, returns true. Else false
     *
     * @return true if this account's {@link UUID} is equal to the argument
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Account)
            if (uuid.equals(((Account) obj).uuid))
                return true;
        return false;
    }

    /**
     *
     * @return Last IP of the player
     */
    public String getIP() {
        return ip;
    }

    /**
     *
     * @return Last online time of the player
     */
    public String getLastOn() {
        return laston;
    }

    /**
     *
     * @return Last Location of the player
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @return Name of the player
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Notes of the player
     */
    public String[] getNotes() {
        return notes;
    }

    /**
     *
     * @return Previous usernames of the Player
     */
    public Response[] getPreviousNames() {
        return names;
    }

    /**
     *
     * @return {@link UUID} of the player
     */
    public UUID getUUID() {
        return uuid;
    }
    
    public boolean exists() {
        return exists;
    }

    /**
     * @return The {@link UUID}'s hashcode.
     */
    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        String str = getClass().getName() + "@" + hashCode() + "[id=" + uuid.toString() + ",name=" + name + ",properties={";
        if (names != null) {
            str += "PreviousNames={";
            for (Response rs : names)
                str += rs.name + ":" + rs.changedToAt + ",";
            str = str.substring(0, str.length() - 1);
            str += "}";
        } else {
            str += "PreviousNames=<null>";
        }
        str += "Notes={";
        for (String note : notes)
            str += note + ",";
        str = str.substring(0, str.length() - 1);
        str += "}}Location=" + location + "LastOn= " + laston + "](/" + ip + ")";
        return str;
    }
}
