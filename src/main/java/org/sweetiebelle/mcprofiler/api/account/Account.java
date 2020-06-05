package org.sweetiebelle.mcprofiler.api.account;

import java.util.Objects;
import java.util.UUID;

import org.sweetiebelle.mcprofiler.api.response.NameResponse;

/**
 * An Account contains a player's UUID, their name, their last online time, their last Location, their last IP and their notes.
 *
 */
public class Account {

    /**
     * The player's last IP.
     */
    private String ip;
    /**
     * The player's last online time.
     */
    private String laston;
    /**
     * The player's last location.
     */
    private String location;
    /**
     * The player's last known name.
     */
    private String name;
    /**
     * The player's previous usernames. Key is the name, String is when they changed at.
     */
    private NameResponse[] names;
    /**
     * The player's notes.
     */
    private Note[] notes;
    /**
     * The UUID of the player.
     */
    private UUID uuid;

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
    public Account(UUID uuid, String name, String laston, String location, String ip, Note[] notes, NameResponse[] names) {
        this.uuid = Objects.requireNonNull(uuid);
        this.name = Objects.requireNonNull(name);
        this.laston = laston == null ? "null" : laston;
        this.location = location;
        this.ip = ip == null ? "null" : ip;
        this.notes = notes;
        this.names = names;
    }

    /**
     * If this Account's uuid equals the argument's {@link UUID}, returns true. Else false
     *
     * @return true if this account's {@link UUID} is equal to the argument
     */
    @Override
    public boolean equals(Object obj) {
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
    public Note[] getNotes() {
        return notes;
    }

    /**
     *
     * @return Previous usernames of the Player
     */
    public NameResponse[] getPreviousNames() {
        return names;
    }

    /**
     *
     * @return {@link UUID} of the player
     */
    public UUID getUUID() {
        return uuid;
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
            for (NameResponse rs : names)
                str += rs.name + ":" + rs.changedToAt + ",";
            str = str.substring(0, str.length() - 1);
            str += "}";
        } else
            str += "PreviousNames=<null>";
        str += "Notes={";
        for (Note note : notes)
            str += note.toString() + ",";
        str = str.substring(0, str.length() - 1);
        str += "}}Location=" + location + "LastOn= " + laston + "](/" + ip + ")";
        return str;
    }
}
