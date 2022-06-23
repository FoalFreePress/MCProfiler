package net.shonx.mcprofiler;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import net.shonx.mcprofiler.api.account.Account;
import net.shonx.mcprofiler.api.account.ConsoleAccount;
import net.shonx.mcprofiler.api.account.alternate.BaseAccount;
import net.shonx.mcprofiler.api.account.alternate.UUIDAlt;

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

    public CompletableFuture<Void> addNote(Account sender, Account target, String note) {
        return Scheduler.makeFuture(() -> {
            data.addNoteToUser(target.getUUID(), sender.getUUID(), note);
        });
    }

    public CompletableFuture<Optional<Account>> getAccount(String playerName) {
        return getAccount(playerName, false);
    }

    public CompletableFuture<Optional<Account>> getAccount(String playerName, boolean needsLastNames) {
        return Scheduler.makeFuture(() -> {
            Objects.requireNonNull(playerName);
            if (playerName.toLowerCase().equals(ConsoleAccount.getInstance().getName().toLowerCase()))
                return Optional.of(ConsoleAccount.getInstance());
            return data.getAccount(playerName, needsLastNames);
        });
    }

    public CompletableFuture<Optional<Account>> getAccount(UUID playerUUID) {
        return getAccount(playerUUID, false);
    }

    public CompletableFuture<Optional<Account>> getAccount(UUID playerUUID, boolean needsLastNames) {
        return Scheduler.makeFuture(() -> {
            Objects.requireNonNull(playerUUID);
            if (playerUUID.equals(ConsoleAccount.getInstance().getUUID()))
                return Optional.of(ConsoleAccount.getInstance());
            return data.getAccount(playerUUID, needsLastNames);
        });
    }

    public CompletableFuture<Account[]> getAccounts(String ip) {
        return Scheduler.makeFuture(() -> {
            ArrayList<String> uuids = data.getUsersAssociatedWithIP(ip);
            ArrayList<Account> accounts = new ArrayList<Account>(uuids.size());
            for (String uuid : uuids) {
                accounts.add(getAccountNoFuture(UUID.fromString(uuid)));
            }
            return accounts.toArray(new Account[0]);
        });
    }

    /**
     * This method returns an Account without being wrapped in a CompletableFuture.
     * <p>
     * This will run on the same thread the moment this function is called.
     * </p>
     * <p>
     * In addition, it will also automatically call the {@link Optional#get()} method on the Optional that normally return.
     * </p>
     * 
     * @deprecated this method is dangerous and bypasses sanity checks. You should only use this if you know what you're doing.
     * @param uuid
     *            the player's UUID
     * @return the Account
     * @throws NoSuchElementException
     *             if the player's UUID doesn't actually have an account.
     * 
     */
    @Deprecated
    public Account getAccountNoFuture(UUID uuid) throws NoSuchElementException {
        if (uuid.equals(ConsoleAccount.getInstance().getUUID()))
            return ConsoleAccount.getInstance();
        return data.getAccount(uuid, false).get();
    }

    /**
     * If isRecursive is true, it retruns {@link net.shonx.mcprofiler.api.account.alternate.AltAccount}. If it is false, then it returns {@link net.shonx.mcprofiler.api.account.alternate.UUIDAlt}
     *
     * @param uuid
     * @param isRecursive
     * @return
     */
    public CompletableFuture<UUIDAlt[]> getAccounts(UUID uuid, boolean isRecursive) {
        return Scheduler.makeFuture(() -> {
            Objects.requireNonNull(uuid);
            UUIDAlt thisUUID = new UUIDAlt(uuid, null);
            ArrayList<? extends BaseAccount> accounts = data.getAltsOfPlayer(uuid, isRecursive);
            ArrayList<UUIDAlt> alts = new ArrayList<UUIDAlt>(accounts.size());
            for (BaseAccount object : accounts) {
                UUIDAlt newAlt = new UUIDAlt(object.getUUID(), object.getIP());
                if (!alts.contains(newAlt) && !thisUUID.equals(newAlt))
                    alts.add(newAlt);
            }
            return alts.toArray(new UUIDAlt[alts.size()]);
        });
    }

    public CompletableFuture<String[]> getIPs(Account account) {
        return Scheduler.makeFuture(() -> {
            Objects.requireNonNull(account);
            return data.getIPsByPlayer(account).toArray(new String[0]);
        });
    }

    public CompletableFuture<Void> savePlayerInformation(Account account) {
        return Scheduler.makeFuture(() -> {
            data.storePlayerIP(account.getUUID(), account.getIP());
            data.updatePlayerInformation(account);
            data.setPlayerLastPosition(account.getUUID(), account.getLocation());
        });
    }

    public CompletableFuture<Void> createPlayerInformation(Account account) {
        return Scheduler.makeFuture(() -> {
            data.createProfile(account);
        });
    }
}
