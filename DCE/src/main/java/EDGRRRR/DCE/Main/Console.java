package EDGRRRR.DCE.Main;

import org.bukkit.ChatColor;

public class Console {
    public ChatColor infoColour = ChatColor.GREEN;
    public ChatColor warnColour = ChatColor.YELLOW;
    public ChatColor severeColour = ChatColor.RED;
    public ChatColor prefixColour = ChatColor.AQUA;
    public String prefix = prefixColour + "[DCE v" + App.get().getDescription().getVersion() + "] - ";
    private void send(String message) {
        App.get().getServer().getConsoleSender().sendMessage(prefix + message);
    }
    
    public void info(String message) {
        send(ChatColor.GREEN + message);
    }

    public void warn(String message) {
        send(ChatColor.YELLOW + message);
    }

    public void severe(String message) {
        send(ChatColor.RED + message);
    }

}
