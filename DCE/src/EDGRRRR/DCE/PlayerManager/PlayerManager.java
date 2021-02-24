package EDGRRRR.DCE.PlayerManager;

import EDGRRRR.DCE.Main.DCEPlugin;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerManager {
    private final DCEPlugin app;

    public PlayerManager(DCEPlugin app) {
        this.app = app;
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
    public OfflinePlayer getOfflinePlayer(String name, boolean allowFetch) {
        name = name.trim().toLowerCase();
        OfflinePlayer[] oPlayers = this.app.getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : oPlayers) {
            String oPlayerName = oPlayer.getName().trim().toLowerCase();
            if (oPlayerName.equals(name)) {
                return oPlayer;
            }
        }

        if (allowFetch) {
            return this.app.getServer().getOfflinePlayer(name);
        } else {
            return null;
        }
    }

    /**
     * Gets an offline player by their UUID
     * Scans only local players unless allowFetch is enabled, which will allow it to scan the web
     *
     * @param uuid       - The uuid the find
     * @param allowFetch - Whether to scan the web or not
     * @return OfflinePlayer - can be null.
     */
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
    public OfflinePlayer getOfflinePlayerByUUID(UUID uuid, boolean allowFetch) {
        OfflinePlayer[] offlinePlayers = this.app.getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : offlinePlayers) {
            if (oPlayer.getUniqueId().equals(uuid)) {
                return oPlayer;
            }
        }

        if (allowFetch) {
            return this.app.getServer().getOfflinePlayer(uuid);
        } else {
            return null;
        }
    }
}
