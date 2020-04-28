package org.sweetiebelle.mcprofiler.api.account.alternate;

import java.util.UUID;

/**
 * Performs .equals() on both UUID and IP
 *
 */
public class AltAccount extends BaseAccount {

    public AltAccount(final UUID uuid, final String ip) {
        super(uuid, ip);
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