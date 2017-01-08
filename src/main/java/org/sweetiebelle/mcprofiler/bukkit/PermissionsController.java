package org.sweetiebelle.mcprofiler.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;

/**
 * This class will allow for multiple Permissions Plugins to be easily
 * supported.
 *
 */
class PermissionsController {
    private Chat chat;

    PermissionsController() {

        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        this.chat = rsp.getProvider();

    }

    /**
     * Worker class for getting a prefix from a permissions plugin.
     *
     * @param uuid player UUID
     * @return the prefix, or &b if no prefix providing plugin was found.
     */
    public String getPrefix(final UUID uuid) {

        if (chat != null) {
            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(uuid);
            String pref = chat.getPlayerPrefix((String) null, player);
            return pref.replace('&', ChatColor.COLOR_CHAR);
        }
        return ChatColor.AQUA + "";
    }
}