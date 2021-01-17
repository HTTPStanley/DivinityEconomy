package EDGRRRR.DCE.Main;

import org.bukkit.ChatColor;

public class Console {
    private ChatColor infoColour = ChatColor.GREEN;
    private ChatColor warnColour = ChatColor.YELLOW;
    private ChatColor severeColour = ChatColor.RED;
    private ChatColor prefixColour = ChatColor.AQUA;
    private String prefix = prefixColour + "[DCE v" + App.get().getDescription().getVersion() + "] - ";
    private void send(String message) {
        App.get().getServer().getConsoleSender().sendMessage(prefix + message);
    }
    
    public void info(String message) {
        send(infoColour + message);
    }

    public void warn(String message) {
        send(warnColour + message);
    }

    public void severe(String message) {
        send(severeColour + message);
    }

}
