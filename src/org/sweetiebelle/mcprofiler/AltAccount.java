package org.sweetiebelle.mcprofiler;

import java.util.UUID;

class AltAccount {
    /**
     * UUID of the alt.
     */
    final UUID uuid;
    /**
     * IP of the alt.
     */
    final String ip;

    AltAccount(final UUID uuid, final String ip) {
        this.uuid = uuid;
        this.ip = ip;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AltAccount) {
            if (uuid.equals(((AltAccount) obj).uuid) && ip.equals(((AltAccount) obj).ip))
                return true;
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "UUID = " + uuid.toString() + ", IP = " + ip;
    }
}
