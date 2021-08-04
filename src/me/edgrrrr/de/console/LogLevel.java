package me.edgrrrr.de.console;

import me.edgrrrr.de.config.Setting;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Logger;

public enum LogLevel {
    DEBUG(0, ChatColor.DARK_PURPLE, Setting.CHAT_DEBUG_COLOR.path),
    INFO(1, ChatColor.GREEN, Setting.CHAT_INFO_COLOR.path),
    WARNING(2, ChatColor.YELLOW, Setting.CHAT_WARNING_COLOR.path),
    SEVERE(3, ChatColor.DARK_RED, Setting.CHAT_SEVERE_COLOR.path);

    private final int priority;
    private ChatColor colour;
    private final String colourOption;

    LogLevel(int priority, ChatColor color, String colourOption) {
        this.priority = priority;
        this.colour = color;
        this.colourOption = colourOption;
    }

    public static void loadValuesFromConfig(YamlConfiguration config) {
        for (LogLevel level : values()) {
            String value = config.getString(level.getColourOption());
            try {
                level.setColour(ChatColor.valueOf(value));
            } catch (Exception e) {
                Logger.getLogger("Minecraft").severe(String.format("Exception occurred on log level loading (%s): %s", level, e.getMessage()));
            }
        }
    }

    public int getPriority() {
        return priority;
    }

    public ChatColor getColour() {
        return colour;
    }

    private void setColour(ChatColor colour) {
        this.colour = colour;
    }

    public String getColourOption() {
        return colourOption;
    }

    public boolean hasPriority(LogLevel over) {
        return priority > over.priority;
    }
}
