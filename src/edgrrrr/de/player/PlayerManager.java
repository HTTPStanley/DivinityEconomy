package edgrrrr.de.player;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.DivinityModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

/**
 * A class for managing players
 */
public class PlayerManager extends DivinityModule {

    /**
     * Constructor
     * @param main - The main class
     */
    public PlayerManager(DEPlugin main) {
        super(main);
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {

    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {

    }


    /**
     * If the player is online or offline
     * Equal to player.getPlayer() == null
     * @param player - The player to check
     * @return boolean
     */
    public boolean playerIsOnline(OfflinePlayer player) {
        return (player.getPlayer() == null);
    }

    /**
     * The player, if they are online
     * @param player - The player
     * @return Player
     */
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
    public OfflinePlayer getOfflinePlayer(String name, boolean allowFetch) {
        OfflinePlayer player = null;
        OfflinePlayer[] oPlayers = this.getMain().getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : oPlayers) {
            String oPlayerName = oPlayer.getName();
            if (oPlayerName != null) {
                if (oPlayerName.toLowerCase().trim().equals(name.trim().toLowerCase())) {
                    player = oPlayer;
                    break;
                }
            }
        }

        if (allowFetch && (player == null)) {
            player = this.getMain().getServer().getOfflinePlayer(name);
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
    public OfflinePlayer getOfflinePlayer(UUID uuid, boolean allowFetch) {
        OfflinePlayer player = null;
        OfflinePlayer[] offlinePlayers = this.getMain().getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : offlinePlayers) {
            if (oPlayer.getUniqueId().equals(uuid)) {
                player = oPlayer;
                break;
            }
        }

        if (allowFetch && (player == null)) {
            player = this.getMain().getServer().getOfflinePlayer(uuid);
        }

        return player;
    }

    /**
     * Gets all offline players who's name starts with startswith
     *
     * @param startsWith
     */
    public OfflinePlayer[] getOfflinePlayers(String startsWith) {
        OfflinePlayer[] offlinePlayers = this.getMain().getServer().getOfflinePlayers();
        ArrayList<OfflinePlayer> players = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName() == null) continue;
            if (offlinePlayer.getName().toLowerCase().startsWith(startsWith.toLowerCase(Locale.ROOT))) {
                players.add(offlinePlayer);
            }
        }

        return players.toArray(new OfflinePlayer[0]);
    }

    /**
     * Gets all names of offline players
     */
    public String[] getOfflinePlayerNames() {
        OfflinePlayer[] offlinePlayers = this.getMain().getServer().getOfflinePlayers();
        ArrayList<String> playerNames = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String name = offlinePlayer.getName();
            if (name == null)  continue;

            playerNames.add(name);
        }

        return playerNames.toArray(new String[0]);
    }

    /**
     * Gets all names of offline players who's name starts with startswith
     */
    public String[] getOfflinePlayerNames(String startsWith) {
        OfflinePlayer[] offlinePlayers = this.getOfflinePlayers(startsWith);
        ArrayList<String> playerNames = new ArrayList<>();
        for (OfflinePlayer player : offlinePlayers) {
            playerNames.add(player.getName());
        }

        return playerNames.toArray(new String[0]);
    }
}
