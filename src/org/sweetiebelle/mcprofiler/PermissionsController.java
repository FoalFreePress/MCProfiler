package org.sweetiebelle.mcprofiler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * This class will allow for multiple Permissions Plugins to be easily supported.
 *
 */
class PermissionsController {
    private PermissionManager permissionsex;

    PermissionsController(MCProfilerPlugin p) {
        final Plugin pex = Bukkit.getPluginManager().getPlugin("PermissionsEx");
        if (pex != null) {
            permissionsex = PermissionsEx.getPermissionManager();
            p.getLogger().info("Found PermissionsEx to be used for getting player prefixes.");
        } else
            permissionsex = null;
        if (permissionsex == null)
            p.getLogger().info("No permisisons plugin found. Using default prefix.");
    }

    /**
     * Worker class for getting a prefix from a permissions plugin.
     * @param uuid player UUID
     * @return the prefix, or §b if no prefix providing plugin was found.
     */
    String getPrefix(final UUID uuid) {
        if (permissionsex != null)
            return permissionsex.getUser(uuid).getPrefix().replace('&', '§');
        return "§b";
    }
}
