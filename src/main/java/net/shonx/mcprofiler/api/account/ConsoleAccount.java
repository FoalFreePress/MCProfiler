package net.shonx.mcprofiler.api.account;

import net.shonx.lib.SweetieLib;
import net.shonx.mcprofiler.api.response.NameResponse;

public class ConsoleAccount extends Account {

    private static ConsoleAccount singleton = new ConsoleAccount();

    public static ConsoleAccount getInstance() {
        return singleton;
    }

    private ConsoleAccount() {
        super(SweetieLib.CONSOLE_UUID, SweetieLib.CONSOLE_NAME, "NEVER", "EVERYWHERE", "127.0.0.1", new Note[0], new NameResponse[] { new NameResponse("~CONSOLE", 0) });
    }
}
