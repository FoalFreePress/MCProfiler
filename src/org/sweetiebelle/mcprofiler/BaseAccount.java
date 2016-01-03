package org.sweetiebelle.mcprofiler;

import java.util.UUID;

/**
 * Interface for all other Accounts
 * @author shroo
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
}
