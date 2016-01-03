package org.sweetiebelle.mcprofiler;

import java.util.UUID;

/**
 * Interface for all other Accounts
 *
 */
interface BaseAccount {
    /**
     * Returns the UUID of this alt
     * @return the uuid
     */
    UUID getUUID();

    /**
     * Returns the IP of this alt.
     * @return the ip
     */
    String getIP();

    static UUIDAlt getUUIDFromBase(BaseAccount alt) {
        return new UUIDAlt(alt.getUUID(), alt.getIP());
    }
}
