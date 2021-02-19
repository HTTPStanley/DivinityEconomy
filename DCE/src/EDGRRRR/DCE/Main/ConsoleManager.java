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
        this.debugMode = (this.app.getConfig().getBoolean(this.app.getConf().strChatDebug) || this.app.getConfig().getBoolean(this.app.getConf().strMainDebugMode));
        this.infoColour = this.getColour(this.app.getConf().strChatInfClr);
        this.warnColour = this.getColour(this.app.getConf().strChatWrnClr);
        this.severeColour = this.getColour(this.app.getConf().strChatSvrClr);
        this.debugColour = this.getColour(this.app.getConf().strChatDbgClr);
        this.prefixColour = this.getColour(this.app.getConf().strChatPfxClr);
        this.prefixSepColour = this.getColour(this.app.getConf().strChatPfxSepClr);
        String prefix = this.app.getConfig().getString(this.app.getConf().strChatMsgPfx);
        String conPrefix = this.app.getConfig().getString(this.app.getConf().strChatConsPfx).replace("%V", this.app.getDescription().getVersion());
        String prefixSep = this.app.getConfig().getString(this.app.getConf().strChatPfxSep);
        this.prefix = prefixColour + prefix + prefixSepColour + prefixSep;
        this.conPrefix = prefixColour + conPrefix + prefixSepColour + prefixSep;
    }

    /**
     * Resolves string colours to the corresponding ChatColor object
     * @param optionName - The config option name
     * @return
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
     * @param message
     */
    private void send(String message) {
        this.app.getServer().getConsoleSender().sendMessage(conPrefix + message);
    }

    /**
     * Sends a (default green) message to the console
     * @param message
     */
    public void info(String message) {
        this.send(infoColour + message);
    }

    /**
     * Sends a (default green) message to the console
     * @param message
     */
    public void debug(String message) {
        if (debugMode) this.send(debugColour + message);
    }

    /**
     * Sends a (default yellow) message to the console
     * @param message
     */
    public void warn(String message) {
        this.send(warnColour + message);
    }

    /**
     * Sends a (default red) message to the console
     * @param message
     */
    public void severe(String message) {
        this.send(severeColour + message);
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
        this.warn(player, "Incorrect command usage: " + errorMessage);
        this.warn(player, "Command Usage: "+ commandUsage);
    }

    /**
     * Sends an info message to a player
     * @param player
     * @param message
     */
    public void info(Player player, String message){
        this.send(player, infoColour + message);
    }

    /**
     * Sends a warning message to a player
     * @param player
     * @param message
     */
    public void warn(Player player, String message){
        this.send(player, warnColour + message);
    }
    /**
     * Sends a severe message to a player
     * @param player
     * @param message
     */
    public void severe(Player player, String message){
        this.send(player, severeColour + message);
    }


}
