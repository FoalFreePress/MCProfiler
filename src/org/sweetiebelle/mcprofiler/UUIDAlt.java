package org.sweetiebelle.mcprofiler;

import java.util.UUID;

/**
 * Performs .equals() based on UUIDs
 *
 */
public class UUIDAlt implements BaseAccount {
    /**
     * UUID of the alt.
     */
    private final UUID uuid;
    /**
     * IP of the alt.
     */
    private final String ip;

    public UUIDAlt(final UUID uuid, final String ip) {
        this.uuid = uuid;
        this.ip = ip;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof UUIDAlt) {
            if (uuid.equals(((UUIDAlt) obj).uuid))
                return true;
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "UUID = " + uuid.toString() + ", IP = " + ip;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getIP() {
        return ip;
    }
}
