package net.shonx.mcprofiler.controller.vanish;

import org.bukkit.entity.Player;
import net.shonx.mcprofiler.api.IVanish;

/**
 * No Vanish plugin detected.
 *
 */
public class DefaultVanish implements IVanish {

    @Override
    public boolean canSee(Player admin, Player sender) {
        return true;
    }

}
