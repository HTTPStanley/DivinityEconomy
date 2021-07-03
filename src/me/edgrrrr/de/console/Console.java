package me.edgrrrr.de.console;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class Console extends DivinityModule {
    private final ConsoleCommandSender consoleSender;

    // Settings
    private final boolean debugMode;
    private final String chatPrefix;
    private final String consolePrefix;


    private static final String[] variables = {"<VERSION>"};
    private static final String[] variableValues = {"<VERSION>"};

    public Console(DEPlugin main) {
        super(main);
        this.consoleSender = main.getServer().getConsoleSender();

        // insert version into variables
        Console.variableValues[0] = main.getDescription().getVersion();

        // Get settings
        FileConfiguration conf = main.getConfig();
        this.debugMode = conf.getBoolean(Setting.CHAT_DEBUG_OUTPUT_BOOLEAN.path);
        String prefix = conf.getString(Setting.CHAT_PREFIX_STRING.path);
        String conPrefix = conf.getString(Setting.CHAT_CONSOLE_PREFIX.path);
        this.chatPrefix = insertColours(prefix);
        conPrefix = insertColours(conPrefix);
        this.consolePrefix = insertVariables(conPrefix);
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
     * Sends a formatted message to the console
     * @param level - The log level
     * @param message - The message to send
     * @param args - The arguments
     */
    public void send(LogLevel level, String message, Object... args) {
        this.consoleSender.sendMessage(consolePrefix + level.getColour() + String.format(message, args));
    }

    /**
     * Sends a formatted (default green) message to the console
     *
     * @param message - The message to send
     */
    public void info(String message, Object... args) {
        this.send(LogLevel.INFO, message, args);
    }

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     * @param args - The args
     */
    public void debug(String message, Object... args) {
        if (debugMode) this.send(LogLevel.DEBUG, message, args);
    }

    /**
     * Sends a formatted (default yellow) message to the console
     *
     * @param message - The message to send
     * @param args - The args
     */
    public void warn(String message, Object... args) {
        this.send(LogLevel.WARNING, message, args);
    }

    /**
     * Sends a formatted (default red) message to the console
     *
     * @param message - The message to send
     * @param args - The args
     */
    public void severe(String message, Object... args) {
        this.send(LogLevel.SEVERE, message, args);
    }

    // PLAYER
    /**
     * Sends a formatted message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     * @param args - The args
     */
    public void send(Player player, LogLevel level, String message, Object... args) {
        if (player != null) {
            player.sendMessage(chatPrefix + level.getColour() + String.format(message, args));
        } else {
            this.send(level, message, args);
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
     * Sends a usage command to the console
     */
    public void usage(String errorMessage, String[] usages) {
        this.warn(String.format("Incorrect command usage: %s", errorMessage));
        this.warn(String.format("Command usage: %s", Arrays.toString(usages)));
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
    public void info(Player player, String message, Object... args) {
        this.send(player, LogLevel.INFO, message, args);
    }

    /**
     * Sends a warning message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void warn(Player player, String message, Object... args) {
        this.send(player, LogLevel.WARNING, message, args);
    }

    /**
     * Sends a severe message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void severe(Player player, String message, Object... args) {
        this.send(player, LogLevel.SEVERE, message, args);
    }
}
