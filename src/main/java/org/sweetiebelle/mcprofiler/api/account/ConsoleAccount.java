package org.sweetiebelle.mcprofiler.api.account;

import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.mcprofiler.api.response.NameResponse;

public class ConsoleAccount extends Account {

    private static ConsoleAccount singleton = new ConsoleAccount();

    public static ConsoleAccount getInstance() {
        return singleton;
    }

    private ConsoleAccount() {
        super(SweetieLib.CONSOLE_UUID, "CONSOLE", "NEVER", "EVERYWHERE", "127.0.0.1", new Note[0], new NameResponse[] { new NameResponse("CONSOLE", 0) }, false);
    }
}
