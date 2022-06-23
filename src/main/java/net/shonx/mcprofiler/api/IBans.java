package net.shonx.mcprofiler.api;

import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IBans {
    /**
     * Returns if the array of player UUIDs are banned.<br>
     * <br>
     * The index to the returned array is the same as the player id.<br>
     * <br>
     * For example,<br>
     * * result[0] will say if players[0] is banned.<br>
     * * result[1] will say if players[1] is banned.
     *
     * @param players the array of players to check if they are banned.
     * @return the result of each player. The index to the returned array is the
     *         same as the player id.
     */
    CompletableFuture<boolean[]> isBanned(UUID[] players);

    /**
     * Returns if the player is banned.
     * 
     * @param player the player to question if they are banned.
     * @return if the player is banned.
     */
    CompletableFuture<Boolean> isBanned(UUID player);
    
    
    /**
     * Returns if the player is banned without sanity checks.<br>
     * 
     * This usually means any {@link Optional} are resolved immediately.<br>
     * 
     * {@link CompletableFuture} are resolved immediately, and exceptions are ignored.<br>
     * 
     * @deprecated this method is dangerous and bypasses sanity checks. You should only use this if you know what you're doing.
     */
    @Deprecated
    boolean isBannedDangerous(UUID player);
}
