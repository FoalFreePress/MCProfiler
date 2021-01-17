package org.sweetiebelle.mcprofiler.api.response;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.sweetiebelle.mcprofiler.MCProfiler;
import org.sweetiebelle.mcprofiler.api.IVanish;

public class OnlineResponse implements Runnable {

    public boolean isOnline = false;
    public boolean senderCanSee = false;
    public Player queriedPlayer = null;
    private CommandSender sender;
    private IVanish vc;

    public OnlineResponse(MCProfiler plugin, CommandSender sender, IVanish vc) {
        Bukkit.getScheduler().runTask(plugin, this);
        this.sender = sender;
        this.vc = vc;
    }

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

}
