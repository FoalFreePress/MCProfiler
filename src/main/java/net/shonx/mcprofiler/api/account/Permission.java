package net.shonx.mcprofiler.api.account;

import org.bukkit.permissions.Permissible;

public class Permission {

    private boolean hasBasicInfoName;
    private boolean hasBasicInfoUUID;
    private boolean hasBasicInfoPreviousUsernames;
    private boolean hasInfoOnline;
    private boolean hasInfoIP;
    private boolean hasInfoNotes;
    private boolean hasInfoPosition;
    private boolean canListIPs;
    private boolean canLookup;
    private boolean canAddNote;
    private boolean displayAltOnJoin;
    private boolean canReload;
    private boolean canShowAlts;
    private boolean canShowUUID;

    public Permission(Permissible sender) {
        hasBasicInfoName = sender.hasPermission("mcprofiler.info.basic.name");
        hasBasicInfoUUID = sender.hasPermission("mcprofiler.info.basic.uuid");
        hasBasicInfoPreviousUsernames = sender.hasPermission("mcprofiler.info.basic.previoususernames");
        hasInfoOnline = sender.hasPermission("mcprofiler.info.online");
        hasInfoIP = sender.hasPermission("mcprofiler.info.ip");
        hasInfoNotes = sender.hasPermission("mcprofiler.readnotes");
        hasInfoPosition = sender.hasPermission("mcprofiler.info.position");
        canListIPs = sender.hasPermission("mcprofiler.listips");
        canLookup = sender.hasPermission("mcprofiler.lookup");
        canAddNote = sender.hasPermission("mcprofiler.addnote");
        displayAltOnJoin = sender.hasPermission("mcprofiler.notifiedofalts");
        canReload = sender.hasPermission("mcprofiler.reload");
        canShowAlts = sender.hasPermission("mcprofiler.listlinks");
        canShowUUID = sender.hasPermission("mcprofiler.uuid");
    }
    

    /**
     * @return the hasBasicInfoName
     */
    public boolean canSeeBasicName() {
        return hasBasicInfoName;
    }

    /**
     * @return the hasBasicInfoUUID
     */
    public boolean canSeeBasicUUID() {
        return hasBasicInfoUUID;
    }

    /**
     * @return the hasBasicInfoPreviousUsernames
     */
    public boolean canSeeBasicPreviousUsernames() {
        return hasBasicInfoPreviousUsernames;
    }

    /**
     * @return the hasInfoOnline
     */
    public boolean canSeeOnline() {
        return hasInfoOnline;
    }

    /**
     * @return the hasInfoIP
     */
    public boolean canSeeIP() {
        return hasInfoIP;
    }

    /**
     * @return the hasInfoNotes
     */
    public boolean canReadNotes() {
        return hasInfoNotes;
    }

    /**
     * @return the hasInfoPosition
     */
    public boolean canSeePosition() {
        return hasInfoPosition;
    }

    /**
     * @return the canListIPs
     */
    public boolean canListIPs() {
        return canListIPs;
    }

    /**
     * @return the canLookup
     */
    public boolean canLookupIP() {
        return canLookup;
    }

    /**
     * @return the canAddNote
     */
    public boolean canAddNote() {
        return canAddNote;
    }

    /**
     * @return the displayAltOnJoin
     */
    public boolean canSeeAltsOnPlayerJoin() {
        return displayAltOnJoin;
    }

    /**
     * @return the canReload
     */
    public boolean canReload() {
        return canReload;
    }

    /**
     * @return the canShowAlts
     */
    public boolean canLookupAlts() {
        return canShowAlts;
    }

    /**
     * @return the canShowUUID
     */
    public boolean canUseUUID() {
        return canShowUUID;
    }
}
