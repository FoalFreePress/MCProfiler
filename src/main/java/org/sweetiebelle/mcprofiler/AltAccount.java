package org.sweetiebelle.mcprofiler;

import java.util.UUID;

/**
 * Performs .equals() on both UUID and IP
 *
 */
public class AltAccount implements BaseAccount {
    /**
     * UUID of the alt.
     */
    private final UUID uuid;
    /**
     * IP of the alt.
     */
    private final String ip;

    public AltAccount(final UUID uuid, final String ip) {
        this.uuid = uuid;
        this.ip = ip;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AltAccount) {
            AltAccount alt = (AltAccount) obj;
            if (uuid.equals(alt.uuid) && ip.equals(alt.ip))
                return true;
            return false;
        }
        return false;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        return "UUID = " + uuid.toString() + ", IP = " + ip;
    }
}