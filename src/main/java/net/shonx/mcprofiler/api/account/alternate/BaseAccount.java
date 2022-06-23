package net.shonx.mcprofiler.api.account.alternate;

import java.util.UUID;

/**
 * Interface for all other Accounts
 *
 */
public abstract class BaseAccount {

    /**
     * IP of the alt.
     */
    protected String ip;
    /**
     * UUID of the alt.
     */
    protected UUID uuid;

    BaseAccount(UUID uuid, String ip) {
        this.uuid = uuid;
        this.ip = ip;
    }

    /**
     * Returns the IP of this alt.
     *
     * @return the ip
     */
    public abstract String getIP();

    /**
     * Returns the UUID of this alt
     *
     * @return the uuid
     */
    public abstract UUID getUUID();
}
