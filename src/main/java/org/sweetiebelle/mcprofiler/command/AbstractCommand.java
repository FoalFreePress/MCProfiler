package org.sweetiebelle.mcprofiler.command;

import java.util.Optional;
import java.util.UUID;

import org.sweetiebelle.lib.LuckPermsManager;
import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.api.account.Account;

public abstract class AbstractCommand {

    protected API api;
    protected LuckPermsManager chat;
    
    
    public AbstractCommand(API api, LuckPermsManager chat) {
        this.api = api;
        this.chat = chat;
    }

    /**
     * Worker class for getting an Account from a name or UUID.
     *
     * @param nameoruuid
     * @param needsLastTime
     * @return
     */
    protected Optional<Account> getAccount(String nameoruuid, boolean needsLastNames) {
        try {
            return api.getAccount(UUID.fromString(nameoruuid), needsLastNames);
        } catch (IllegalArgumentException e) {
            return api.getAccount(nameoruuid, needsLastNames);
        }
    }
}
