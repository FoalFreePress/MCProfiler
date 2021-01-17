package org.sweetiebelle.mcprofiler.controller.vanish;

import org.bukkit.entity.Player;
import org.sweetiebelle.mcprofiler.api.IVanish;

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
