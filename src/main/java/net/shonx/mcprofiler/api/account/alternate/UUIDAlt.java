package net.shonx.mcprofiler.api.account.alternate;

import java.util.UUID;

/**
 * Performs .equals() based on UUIDs
 *
 */
public class UUIDAlt extends BaseAccount {

    public UUIDAlt(UUID uuid, String ip) {
        super(uuid, ip);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseAccount)
            if (uuid.equals(((BaseAccount) obj).uuid))
                return true;
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
