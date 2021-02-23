package EDGRRRR.DCE.Main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConsoleManager {
    private final DCEPlugin app;

    // Settings
    private final boolean debugMode;
    private final ChatColor infoColour;
    private final ChatColor warnColour;
    private final ChatColor severeColour;
    private final ChatColor debugColour;
    private final ChatColor prefixColour;
    private final ChatColor prefixSepColour;
    private final String prefix;
    private final String conPrefix;

    // Colours
    private final HashMap<String, ChatColor> colourMap;

    public ConsoleManager(DCEPlugin app){
        this.app = app;

        // Colours :D
        this.colourMap = new HashMap<>();
        this.colourMap.put("AQUA", ChatColor.AQUA);
        this.colourMap.put("BLACK", ChatColor.BLACK);
        this.colourMap.put("BLUE", ChatColor.BLUE);
        this.colourMap.put("DARK_AQUA", ChatColor.DARK_AQUA);
        this.colourMap.put("DARK_BLUE", ChatColor.DARK_BLUE);
        this.colourMap.put("DARK_GRAY", ChatColor.DARK_GRAY);
        this.colourMap.put("DARK_GREEN", ChatColor.DARK_GREEN);
        this.colourMap.put("DARK_PURPLE", ChatColor.DARK_PURPLE);
        this.colourMap.put("DARK_RED", ChatColor.DARK_RED);
        this.colourMap.put("GOLD", ChatColor.GOLD);
        this.colourMap.put("GRAY", ChatColor.GRAY);
        this.colourMap.put("GREEN", ChatColor.GREEN);
        this.colourMap.put("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE);
        this.colourMap.put("MAGIC", ChatColor.MAGIC);
        this.colourMap.put("RED", ChatColor.RED);
        this.colourMap.put("WHITE", ChatColor.WHITE);
        this.colourMap.put("YELLOW", ChatColor.YELLOW);


        // Get settings
        this.debugMode = (this.app.getConfig().getBoolean(this.app.getConfigManager().strChatDebug) || this.app.getConfig().getBoolean(this.app.getConfigManager().strMainDebugMode));
        this.infoColour = this.getColour(this.app.getConfigManager().strChatInfClr);
        this.warnColour = this.getColour(this.app.getConfigManager().strChatWrnClr);
        this.severeColour = this.getColour(this.app.getConfigManager().strChatSvrClr);
        this.debugColour = this.getColour(this.app.getConfigManager().strChatDbgClr);
        this.prefixColour = this.getColour(this.app.getConfigManager().strChatPfxClr);
        this.prefixSepColour = this.getColour(this.app.getConfigManager().strChatPfxSepClr);
        String prefix = this.app.getConfig().getString(this.app.getConfigManager().strChatMsgPfx);
        String conPrefix = this.app.getConfig().getString(this.app.getConfigManager().strChatConsPfx).replace("%V", this.app.getDescription().getVersion());
        String prefixSep = this.app.getConfig().getString(this.app.getConfigManager().strChatPfxSep);
        this.prefix = prefixColour + prefix + prefixSepColour + prefixSep;
        this.conPrefix = prefixColour + conPrefix + prefixSepColour + prefixSep;
    }

    /**
     * Resolves string colours to the corresponding ChatColor object
     * @param optionName - The config option name
     * @return ChatColour - The colour
     */
    private ChatColor getColour(String optionName) {
        String option = this.app.getConfig().getString(optionName);
        String defaultOption = this.app.getConfig().getDefaults().getString(optionName);
        ChatColor colour = this.colourMap.get(option);
        if (colour == null) {
            colour = this.colourMap.get(defaultOption);
        }

        return colour;
    }

    // CONSOLE COMMANDS
    /**
     * Sends a message to the console
     * @param message - The message to send
     */
    private void send(String message) {
        this.app.getServer().getConsoleSender().sendMessage(conPrefix + message);
    }

    /**
     * Sends a (default green) message to the console
     * @param message - The message to send
     */
    public void info(String message) {
        this.send(infoColour + message);
    }

    /**
     * Sends a (default green) message to the console
     * @param message - The message to send
     */
    public void debug(String message) {
        if (debugMode) this.send(debugColour + message);
    }

    /**
     * Sends a (default yellow) message to the console
     * @param message - The message to send
     */
    public void warn(String message) {
        this.send(warnColour + message);
    }

    /**
     * Sends a (default red) message to the console
     * @param message - The message to send
     */
    public void severe(String message) {
        this.send(severeColour + message);
    }

    // PLAYER
    /**
     * Sends a message to a player
     * @param player - The player to send to
     * @param message - The message to send
     */
    private void send(Player player, String message) {
        player.sendMessage(prefix + message);
    }

    /**
     * Sends a usage command to a player
     * @param player - The player to send to
     * @param errorMessage - The message to send
     * @param commandUsage - The usage of the command
     */
    public void usage(Player player, String errorMessage, String commandUsage){
        this.warn(player, "Incorrect command usage: " + errorMessage);
        this.warn(player, "Command Usage: "+ commandUsage);
    }

    /**
     * Sends an info message to a player
     * @param player - The player to send to
     * @param message - The message to send
     */
    public void info(Player player, String message){
        this.send(player, infoColour + message);
    }

    /**
     * Sends a warning message to a player
     * @param player - The player to send to
     * @param message - The message to send
     */
    public void warn(Player player, String message){
        this.send(player, warnColour + message);
    }
    /**
     * Sends a severe message to a player
     * @param player - The player to send to
     * @param message - The message to send
     */
    public void severe(Player player, String message){
        this.send(player, severeColour + message);
    }


}
