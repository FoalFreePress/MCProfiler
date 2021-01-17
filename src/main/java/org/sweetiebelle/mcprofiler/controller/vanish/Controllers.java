package org.sweetiebelle.mcprofiler.controller.vanish;

import org.sweetiebelle.mcprofiler.API;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.IVanish;
import org.sweetiebelle.mcprofiler.api.exception.VanishPluginNotLoadedException;

import com.google.common.base.Verify;

public class Controllers {
    public static IVanish vanish = null;
    
    
    public static IVanish getVanish() {
        return Verify.verifyNotNull(vanish);
    }

    public static void getController(MCProfiler mcprofiler, API api) {
        if(vanish != null)
            throw new IllegalStateException("Called multiple times.");
        if (vanish == null)
            if (tryVNP(mcprofiler, api))
                return;
        if (vanish == null)
            if (trySPVanish(mcprofiler, api))
                return;
        vanish = new DefaultVanish();
    }

    private static boolean tryVNP(MCProfiler mcprofiler, API api) {
        try {
            vanish = new VNPVanish(mcprofiler, api);
            return true;
        } catch (VanishPluginNotLoadedException ex) {
            return false;
        }
    }

    private static boolean trySPVanish(MCProfiler mcprofiler, API api) {
        try {
            vanish = new SPVanish(mcprofiler, api);
            return true;
        } catch (VanishPluginNotLoadedException ex) {
            return false;
        }
    }
}
