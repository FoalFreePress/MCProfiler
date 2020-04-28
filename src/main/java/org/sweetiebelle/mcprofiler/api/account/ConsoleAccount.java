package org.sweetiebelle.mcprofiler.api.account;

import org.bukkit.ChatColor;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.NamesFetcher.Response;

public class ConsoleAccount extends Account {

    private static ConsoleAccount singleton = new ConsoleAccount();

    public static ConsoleAccount getInstance() {
        return singleton;
    }

    private ConsoleAccount() {
        super(SweetieLib.CONSOLE_UUID, "CONSOLE", "NEVER", "EVERYWHERE", "127.0.0.1", new String[] { ChatColor.RED + "Console has no notes" }, new Response[] { new Response("CONSOLE", 0) }, false);
    }
}
