package org.sweetiebelle.mcprofiler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.brohoof.brohoofbans.BrohoofBansPlugin;

/**
 * This class will allow for multiple Ban Plugins to be easily supported.
 *
 */
public class BansController {

    /**
     * CommandBook's ban database
     */
    private BrohoofBansPlugin brohoofBans;

    public BansController() {
        Plugin bb = Bukkit.getPluginManager().getPlugin("BrohoofBans");
        if (bb != null)
            brohoofBans = (BrohoofBansPlugin) bb;
        else
            brohoofBans = null;
    }

    /**
     * Worker class
     *
     * @param uuid
     *            the player's uuid
     * @return if the player is banned or not. New plugins must support uuid lookup.
     */
    public boolean isBanned(UUID uuid) {
        if (brohoofBans != null)
            return BrohoofBansPlugin.getAPI().isBanned(uuid);
        return Bukkit.getOfflinePlayer(uuid).isBanned();
    }
}
