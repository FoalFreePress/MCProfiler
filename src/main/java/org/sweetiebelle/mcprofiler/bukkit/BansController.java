package org.sweetiebelle.mcprofiler.bukkit;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.bans.BanDatabase;
import com.sk89q.commandbook.bans.BansComponent;

/**
 * This class will allow for multiple Ban Plugins to be easily supported.
 *
 */
class BansController {

    /**
     * CommandBook's ban database
     */
    private BanDatabase commandbook;

    BansController() {
        final Plugin cb = Bukkit.getPluginManager().getPlugin("CommandBook");
        if (cb != null) {
            commandbook = ((CommandBook) cb).getComponentManager().getComponent(BansComponent.class).getBanDatabase();
            LogManager.getLogger().info("Found CommandBook! Using it for ban lookups.");
        } else
            commandbook = null;
        if (commandbook == null)
            LogManager.getLogger().info("No bans plugin found. Using Bukkit's ban system.");
    }

    /**
     * Worker class
     * 
     * @param uuid
     *            the player's uuid
     * @return if the player is banned or not. New plugins must support uuid lookup.
     */
    boolean isBanned(final UUID uuid) {
        if (commandbook != null)
            return commandbook.isBanned(uuid);
        return Bukkit.getOfflinePlayer(uuid).isBanned();
    }
}
