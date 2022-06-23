package net.shonx.mcprofiler.api;

import org.bukkit.entity.Player;

public interface IVanish {
    /**
     * Checks if the sender can see the admin
     *
     * @param admin  the player to check if they are vanished
     * @param sender the player
     * @return true if they can see them, else false
     */
    boolean canSee(Player admin, Player sender);
}
