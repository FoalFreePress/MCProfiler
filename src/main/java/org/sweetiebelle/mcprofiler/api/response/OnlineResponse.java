package org.sweetiebelle.mcprofiler.api.response;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.controller.VanishController;

public class OnlineResponse {

    public boolean isOnline = false;
    public boolean senderCanSee = false;
    public Player queriedPlayer = null;

    public OnlineResponse(MCProfiler plugin, CommandSender sender, VanishController vc) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (sender instanceof Player) {
                    for (Player player : Bukkit.getServer().getOnlinePlayers())
                        if (player.getName().equals(sender.getName())) {
                            isOnline = true;
                            senderCanSee = vc.canSee(player, (Player) sender);
                            queriedPlayer = player;
                        }
                    return;
                }
                for (Player player : Bukkit.getServer().getOnlinePlayers())
                    if (player.getName().equals(sender.getName())) {
                        isOnline = true;
                        senderCanSee = true;
                        queriedPlayer = player;
                    }
            }
        });
    }
}
