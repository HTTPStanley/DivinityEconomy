package edgrrrr.paa.playerManager;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerManagerAPI {

    /**
     * If the player is online or offline
     * Equal to player.getPlayer() == null
     * @param player - The player to check
     * @return boolean
     */
    public boolean playerIsOnline(OfflinePlayer player);

    /**
     * The player, if they are online
     * @param player - The player
     * @return Player
     */
    public Player getPlayer(OfflinePlayer player);

    /**
     * Returns an offline player
     * Scans local offline players
     * If allow fetch is enabled, then will find fetch player from the web.
     *
     * @param name       - name to scan for.
     * @param allowFetch - Uses deprecated "bukkit.getOfflinePlayer".
     * @return OfflinePlayer - the player corresponding to the name.
     */
    public OfflinePlayer getOfflinePlayer(String name, boolean allowFetch);

    /**
     * Gets an offline player by their UUID
     * Scans only local players unless allowFetch is enabled, which will allow it to scan the web
     *
     * @param uuid       - The uuid the find
     * @param allowFetch - Whether to scan the web or not
     * @return OfflinePlayer - can be null.
     */
    public OfflinePlayer getOfflinePlayer(UUID uuid, boolean allowFetch);

    /**
     * Gets all offline players who's name starts with startswith
     */
    public OfflinePlayer[] getOfflinePlayers(String startsWith);

    /**
     * Gets all names of offline players
     */
    public String[] getOfflinePlayerNames();

    /**
     * Gets all names of offline players who's name starts with startswith
     */
    public String[] getOfflinePlayerNames(String startsWith);
}
