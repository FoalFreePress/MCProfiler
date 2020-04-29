package org.sweetiebelle.mcprofiler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.sweetiebelle.lib.SweetieLib;
import org.sweetiebelle.lib.exceptions.NoDataException;
import org.sweetiebelle.mcprofiler.api.account.Account;
import org.sweetiebelle.mcprofiler.api.account.ConsoleAccount;
import org.sweetiebelle.mcprofiler.api.account.alternate.BaseAccount;
import org.sweetiebelle.mcprofiler.api.account.alternate.UUIDAlt;

public class API {

    public static final String locationToString(Location location) {
        Objects.requireNonNull(location);
        World world = location.getWorld();
        return String.format("%s:%d,%d,%d", world == null ? "%UNLOADED_WORLD%" : world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static final Location stringToLocation(String location) {
        if (location == null)
            return new Location(null, 0, 0, 0);
        String[] parts = location.split(":");
        String[] coords = parts[1].split(",");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
    }

    private Data data;

    API(Data data) {
        this.data = data;
    }

    public void addNote(Account sender, Account target, String note) {
        data.addNoteToUser(target.getUUID(), sender.getUUID(), note);
    }

    public Optional<Account> getAccount(String playerName) {
        return getAccount(playerName, false);
    }

    public Optional<Account> getAccount(String playerName, boolean needsLastNames) {
        Objects.requireNonNull(playerName);
        return data.getAccount(playerName, needsLastNames);
    }

    public Optional<Account> getAccount(UUID playerUUID) {
        return getAccount(playerUUID, false);
    }

    public Optional<Account> getAccount(UUID playerUUID, boolean needsLastNames) {
        Objects.requireNonNull(playerUUID);
        if (playerUUID.equals(SweetieLib.CONSOLE_UUID))
            return Optional.of(ConsoleAccount.getInstance());
        return data.getAccount(playerUUID, needsLastNames);
    }

    public Account[] getAccounts(String ip) {
        ArrayList<String> uuids = data.getUsersAssociatedWithIP(ip);
        ArrayList<Account> accounts = new ArrayList<Account>(uuids.size());
        for (String uuid : uuids) {
            Optional<Account> a = getAccount(UUID.fromString(uuid));
            if (a.isPresent())
                accounts.add(a.get());
            else
                throw new RuntimeException(new NoDataException("UUID " + uuid + " did not an account, but it appeard in the ip table."));
        }
        return accounts.toArray(new Account[0]);
    }

    /**
     * If isRecursive is true, it retruns {@link org.sweetiebelle.mcprofiler.api.account.alternate.AltAccount}. If it is false, then it returns {@link org.sweetiebelle.mcprofiler.api.account.alternate.UUIDAlt}
     *
     * @param uuid
     * @param isRecursive
     * @return
     */
    public UUIDAlt[] getAccounts(UUID uuid, boolean isRecursive) {
        UUIDAlt thisUUID = new UUIDAlt(uuid, null);
        ArrayList<? extends BaseAccount> accounts = data.getAltsOfPlayer(uuid, isRecursive);
        ArrayList<UUIDAlt> alts = new ArrayList<UUIDAlt>(accounts.size());
        for (BaseAccount object : accounts) {
            UUIDAlt newAlt = new UUIDAlt(object.getUUID(), object.getIP());
            if (!alts.contains(newAlt) || thisUUID.equals(object))
                alts.add(newAlt);
        }
        return alts.toArray(new UUIDAlt[alts.size()]);
    }

    public String[] getIPs(Account account) {
        Objects.requireNonNull(account);
        return data.getIPsByPlayer(account).toArray(new String[0]);
    }

    void updatePlayerInformation(Account account) {
        Objects.requireNonNull(account);
        data.storePlayerIP(account.getUUID(), account.getIP());
        if (account.exists()) {
            data.updatePlayerInformation(account);
            data.setPlayerLastPosition(account.getUUID(), API.stringToLocation(account.getLocation()));
        } else
            data.createProfile(account);
    }
}
