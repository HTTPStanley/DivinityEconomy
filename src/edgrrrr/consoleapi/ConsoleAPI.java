package edgrrrr.consoleapi;

import org.bukkit.entity.Player;

/**
 * Console class for sending uniform messages to players and the console.
 */
public interface ConsoleAPI {

    /**
     * Sends a message to the console
     *
     * @param message - The message to send
     */
    public void send(LogLevel level, String message);

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void info(String message);

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void debug(String message);

    /**
     * Sends a (default yellow) message to the console
     *
     * @param message - The message to send
     */
    public void warn(String message);

    /**
     * Sends a (default red) message to the console
     *
     * @param message - The message to send
     */
    public void severe(String message);

    // PLAYER

    /**
     * Sends a message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void send(Player player, LogLevel level, String message);

    /**
     * Sends a help message to a player
     * @param player
     * @param command
     * @param description
     * @param usages
     * @param aliases
     */
    public void help(Player player, String command, String description, String[] usages, String[] aliases);

    /**
     * Sends a usage command to a player
     *
     * @param player       - The player to send to
     * @param errorMessage - The message to send
     */
    public void usage(Player player, String errorMessage, String[] usage);

    /**
     * Sends an info message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void info(Player player, String message);

    /**
     * Sends a warning message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void warn(Player player, String message);

    /**
     * Sends a severe message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void severe(Player player, String message);
}
