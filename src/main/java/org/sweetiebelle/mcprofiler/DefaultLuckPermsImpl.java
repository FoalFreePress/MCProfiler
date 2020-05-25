package org.sweetiebelle.mcprofiler;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.sweetiebelle.lib.LuckPermsManager;


public class DefaultLuckPermsImpl implements LuckPermsManager {

    @Override
    public String getCompletePlayerPrefix(UUID arg0) {
        return ChatColor.AQUA + "";
    }

    @Override
    public String getCompletePlayerSuffix(UUID arg0) {
        return ChatColor.RESET + "";
    }

    @Override
    public String getGroupPrefix(UUID arg0) {
        return ChatColor.AQUA + "";
    }

    @Override
    public String getGroupSuffix(UUID arg0) {
        return ChatColor.RESET + "";
    }

    @Override
    public String getPlayerPrefix(UUID arg0) {
        return ChatColor.AQUA + "";
    }

    @Override
    public String getPlayerSuffix(UUID arg0) {
        return ChatColor.RESET + "";
    }
}
