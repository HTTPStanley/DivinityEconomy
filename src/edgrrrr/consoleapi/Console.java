package edgrrrr.consoleapi;

import edgrrrr.configapi.Setting;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class Console implements ConsoleAPI{
    private final ConsoleCommandSender consoleSender;

    // Settings
    private final boolean debugMode;
    private final String chatPrefix;
    private final String consolePrefix;


    private static final String[] variables = {"<VERSION>"};
    private static final String[] variableValues = {"<VERSION>"};

    public Console(JavaPlugin plugin) {
        this.consoleSender = plugin.getServer().getConsoleSender();

        // insert version into variables
        Console.variableValues[0] = plugin.getDescription().getVersion();

        // Get settings
        FileConfiguration conf = plugin.getConfig();
        this.debugMode = conf.getBoolean(Setting.CHAT_DEBUG_OUTPUT_BOOLEAN.path);
        String prefix = conf.getString(Setting.CHAT_PREFIX_STRING.path);
        String conPrefix = conf.getString(Setting.CHAT_CONSOLE_PREFIX.path);
        this.chatPrefix = insertColours(prefix);
        conPrefix = insertColours(conPrefix);
        this.consolePrefix = insertVariables(conPrefix);
    }

    private static String insertVariables(String string) {
        for (int idx=0; idx < variables.length; idx++) {
            string = string.replace(variables[idx], variableValues[idx]);
        }
        return string;
    }

    private static String insertColours(String string) {
        for (ChatColor colour : ChatColor.values()) {
            string = string.replaceAll(String.format("(&%s)|(%s)", colour.getChar(), colour.name()), colour.toString());
        }
        return string;
    }

    // CONSOLE COMMANDS

    /**
     * Sends a message to the console
     *
     * @param message - The message to send
     */
    public void send(LogLevel level, String message) {
        this.consoleSender.sendMessage(consolePrefix + level.getColour() + message);
    }

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void info(String message) {
        this.send(LogLevel.INFO, message);
    }

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void debug(String message) {
        if (debugMode) this.send(LogLevel.DEBUG, message);
    }

    /**
     * Sends a (default yellow) message to the console
     *
     * @param message - The message to send
     */
    public void warn(String message) {
        this.send(LogLevel.WARNING, message);
    }

    /**
     * Sends a (default red) message to the console
     *
     * @param message - The message to send
     */
    public void severe(String message) {
        this.send(LogLevel.SEVERE, message);
    }

    // PLAYER

    /**
     * Sends a message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void send(Player player, LogLevel level, String message) {
        if (player != null) {
            player.sendMessage(chatPrefix + level.getColour() + message);
        } else {
            this.send(level, message);
        }
    }

    /**
     * Sends a usage command to a player
     */
    public void usage(Player player, String errorMessage, String[] usages) {
        this.warn(player, String.format("Incorrect command usage: %s", errorMessage));
        this.warn(player, String.format("Command usage: %s", Arrays.toString(usages)));
    }

    /**
     * Sends a help message to a player
     * @param player
     * @param command
     * @param description
     * @param usages
     * @param aliases
     */
    public void help(Player player, String command, String description, String[] usages, String[] aliases) {
        this.info(player, String.format("Help for %s", command));
        this.info(player, String.format("Description: %s", description));
        this.info(player, String.format("Usages: %s", Arrays.toString(usages)));
        this.info(player, String.format("Aliases: %s", Arrays.toString(aliases)));
    }

    /**
     * Sends an info message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void info(Player player, String message) {
        this.send(player, LogLevel.INFO, message);
    }

    /**
     * Sends a warning message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void warn(Player player, String message) {
        this.send(player, LogLevel.WARNING, message);
    }

    /**
     * Sends a severe message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void severe(Player player, String message) {
        this.send(player, LogLevel.SEVERE, message);
    }
}
