package org.sweetiebelle.mcprofiler;

import java.util.UUID;

import org.sweetiebelle.mcprofiler.NamesFetcher.Response;

/**
 * An Account contains a player's UUID, their name, their last online time, their last Location, their last IP and their notes.
 *
 */
class Account implements BaseAccount {
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
    Account(final UUID uuid, final String name, final String laston, final String location, final String ip, final String[] notes, final Response[] names) {
        this.uuid = uuid;
        this.name = name;
        this.laston = laston;
        this.location = location;
        this.ip = ip;
        this.notes = notes;
        this.names = names;
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
    @Override
    public String getIP() {
        return ip;
    }

    /**
     *
     * @return Last online time of the player
     */
    String getLastOn() {
        return laston;
    }

    /**
     *
     * @return Last Location of the player
     */
    String getLocation() {
        return location;
    }

    /**
     *
     * @return Name of the player
     */
    String getName() {
        return name;
    }

    /**
     *
     * @return Notes of the player
     */
    String[] getNotes() {
        return notes;
    }

    /**
     *
     * @return Previous usernames of the Player
     */
    Response[] getPreviousNames() {
        return names;
    }

    /**
     *
     * @return {@link UUID} of the player
     */
    @Override
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
        return "Account{" + uuid.toString() + ", " + name + "}";
    }
}
