package org.sweetiebelle.mcprofiler.accounts;

import java.util.UUID;

/**
 * Interface for all other Accounts
 *
 */
public abstract class BaseAccount {

    /**
     * UUID of the alt.
     */
    protected final UUID uuid;
    /**
     * IP of the alt.
     */
    protected final String ip;

    BaseAccount(UUID uuid, String ip) {
        this.uuid = uuid;
        this.ip = ip;
    }

    /**
     * Returns the UUID of this alt
     * 
     * @return the uuid
     */
    public abstract UUID getUUID();

    /**
     * Returns the IP of this alt.
     * 
     * @return the ip
     */
    public abstract String getIP();
}
