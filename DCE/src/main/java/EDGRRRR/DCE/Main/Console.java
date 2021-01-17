package EDGRRRR.DCE.Main;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class Console {
    private ChatColor infoColour = ChatColor.GREEN;
    private ChatColor warnColour = ChatColor.YELLOW;
    private ChatColor severeColour = ChatColor.RED;
    private ChatColor prefixColour = ChatColor.AQUA;
    private String prefix = prefixColour + "[DCE] - ";
    private String conPrefix = prefixColour + "[DCE v" + App.get().getDescription().getVersion() + "] - ";

    // CONSOLE COMMANDS
    /**
     * Sends a message to the console
     * @param message
     */
    private void send(String message) {
        App.get().getServer().getConsoleSender().sendMessage(conPrefix + message);
    }
    
    /**
     * Sends a (default green) message to the console
     * @param message
     */
    public void info(String message) {
        send(infoColour + message);
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
