package org.sweetiebelle.mcprofiler.api.account;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Note {

    private String staffName;
    private UUID staffUUID;
    private String time;
    private String note;

    public Note(UUID staffUUID, String staffName, String time, String note) {
        this.staffName = staffName;
        this.staffUUID = staffUUID;
        this.time = time;
        this.note = note;
    }

    public String staffName() {
        return staffName;
    }

    @Override
    public String toString() {
        return ChatColor.RED + time + ChatColor.RESET + " " + ChatColor.WHITE + note + ChatColor.RESET + " " + ChatColor.RED + staffName();
    }

    public void getStaffName() {
        if (staffUUID.equals(ConsoleAccount.getInstance().getUUID()))
            staffName = ConsoleAccount.getInstance().getName();
        else
            staffName = Bukkit.getOfflinePlayer(staffUUID).getName();
    }
}
