package EDGRRRR.DCE.Player;

import EDGRRRR.DCE.Main.DCEPlugin;
import com.sun.istack.internal.NotNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A class for managing players
 */
public class PlayerManager {
    private final DCEPlugin app;

    /**
     * Constructor
     * @param app - The main class
     */
    public PlayerManager(DCEPlugin app) {
        this.app = app;
    }


    /**
     * If the player is online or offline
     * Equal to player.getPlayer() == null
     * @param player - The player to check
     * @return boolean
     */
    @NotNull
    public boolean playerIsOnline(OfflinePlayer player) {
        return (player.getPlayer() == null);
    }

    /**
     * The player, if they are online
     * @param player - The player
     * @return Player
     */
    @Nullable
    public Player getPlayer(OfflinePlayer player) {
        return player.getPlayer();
    }

    /**
     * Returns an offline player
     * Scans local offline players
     * If allow fetch is enabled, then will find fetch player from the web.
     *
     * @param name       - name to scan for.
     * @param allowFetch - Uses deprecated "bukkit.getOfflinePlayer".
     * @return OfflinePlayer - the player corresponding to the name.
     */
    @Nullable
    public OfflinePlayer getOfflinePlayer(String name, boolean allowFetch) {
        OfflinePlayer player = null;
        name = name.trim().toLowerCase();
        OfflinePlayer[] oPlayers = this.app.getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : oPlayers) {
            String oPlayerName = oPlayer.getName().trim().toLowerCase();
            if (oPlayerName.equals(name)) {
                player = oPlayer;
                break;
            }
        }

        if (allowFetch && (player == null)) {
            player = this.app.getServer().getOfflinePlayer(name);
        }

        return player;
    }

    /**
     * Gets an offline player by their UUID
     * Scans only local players unless allowFetch is enabled, which will allow it to scan the web
     *
     * @param uuid       - The uuid the find
     * @param allowFetch - Whether to scan the web or not
     * @return OfflinePlayer - can be null.
     */
    @Nullable
    public OfflinePlayer getOfflinePlayerByUUID(String uuid, boolean allowFetch) {
        return this.getOfflinePlayerByUUID(UUID.fromString(uuid), allowFetch);
    }

    /**
     * Gets an offline player by their UUID
     * Scans only local players unless allowFetch is enabled, which will allow it to scan the web
     *
     * @param uuid       - The uuid the find
     * @param allowFetch - Whether to scan the web or not
     * @return OfflinePlayer - can be null.
     */
    @Nullable
    public OfflinePlayer getOfflinePlayerByUUID(UUID uuid, boolean allowFetch) {
        OfflinePlayer player = null;
        OfflinePlayer[] offlinePlayers = this.app.getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : offlinePlayers) {
            if (oPlayer.getUniqueId().equals(uuid)) {
                player = oPlayer;
                break;
            }
        }

        if (allowFetch && (player == null)) {
            player = this.app.getServer().getOfflinePlayer(uuid);
        }

        return player;
    }
}
