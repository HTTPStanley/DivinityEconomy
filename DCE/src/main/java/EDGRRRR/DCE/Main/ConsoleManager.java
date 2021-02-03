package EDGRRRR.DCE.Main;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConsoleManager {
    private DCEPlugin app;

    // Settings
    private boolean debugMode;
    private ChatColor infoColour;
    private ChatColor warnColour;
    private ChatColor severeColour;
    private ChatColor debugColour;
    private ChatColor prefixColour;
    private ChatColor prefixSepColour;
    private String prefix;
    private String conPrefix;

    // Colours
    private HashMap<String, ChatColor> colourMap;

    public ConsoleManager(DCEPlugin app){
        this.app = app;

        // Colours :D
        this.colourMap = new HashMap<String, ChatColor>();
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
        this.debugMode = (app.getConfig().getBoolean(app.getConf().strChatDebug) || app.getConfig().getBoolean(app.getConf().strMainDebugMode));
        this.infoColour = getColour(app.getConf().strChatInfClr);
        this.warnColour = getColour(app.getConf().strChatWrnClr);
        this.severeColour = getColour(app.getConf().strChatSvrClr);
        this.debugColour = getColour(app.getConf().strChatDbgClr);
        this.prefixColour = getColour(app.getConf().strChatPfxClr);
        this.prefixSepColour = getColour(app.getConf().strChatPfxSepClr);
        String prefix = app.getConfig().getString(app.getConf().strChatMsgPfx);
        String conPrefix = app.getConfig().getString(app.getConf().strChatConsPfx).replace("%V", app.getDescription().getVersion());
        String prefixSep = app.getConfig().getString(app.getConf().strChatPfxSep);
        this.prefix = prefixColour + prefix + prefixSepColour + prefixSep;
        this.conPrefix = prefixColour + conPrefix + prefixSepColour + prefixSep;
    }

    /**
     * Resolves string colours to the corresponding ChatColor object
     * @param optionName - The config option name
     * @return
     */
    private ChatColor getColour(String optionName) {
        String option = app.getConfig().getString(optionName);
        String defaultOption = app.getConfig().getDefaults().getString(optionName);
        ChatColor colour = colourMap.get(option);
        if (colour == null) {
            colour = colourMap.get(defaultOption);
        }

        return colour;
    }

    // CONSOLE COMMANDS
    /**
     * Sends a message to the console
     * @param message
     */
    private void send(String message) {
        app.getServer().getConsoleSender().sendMessage(conPrefix + message);
    }

    /**
     * Sends a (default green) message to the console
     * @param message
     */
    public void info(String message) {
        send(infoColour + message);
    }

    /**
     * Sends a (default green) message to the console
     * @param message
     */
    public void debug(String message) {
        if (debugMode == true) {
            send(debugColour + message);
        }
    }

    /**
     * Sends a (default yellow) message to the console
     * @param message
     */
    public void warn(String message) {
        send(warnColour + message);
    }

    /**
     * Sends a (default red) message to the console
     * @param message
     */
    public void severe(String message) {
        send(severeColour + message);
    }

    // PLAYER
    /**
     * Sends a message to a player
     * @param player
     * @param message
     */
    private void send(Player player, String message) {
        player.sendMessage(prefix + message);
    }

    /**
     * Sends a usage command to a player
     * @param player
     * @param message
     */
    public void usage(Player player, String errorMessage, String commandUsage){
        warn(player, "Incorrect command usage: " + errorMessage);
        warn(player, "Command Usage: "+ commandUsage);
    }

    /**
     * Sends an info message to a player
     * @param player
     * @param message
     */
    public void info(Player player, String message){
        send(player, infoColour + message);
    }

    /**
     * Sends a warning message to a player
     * @param player
     * @param message
     */
    public void warn(Player player, String message){
        send(player, warnColour + message);
    }
    /**
     * Sends a severe message to a player
     * @param player
     * @param message
     */
    public void severe(Player player, String message){
        send(player, severeColour + message);
    }


}
