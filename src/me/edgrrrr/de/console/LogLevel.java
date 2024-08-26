package me.edgrrrr.de.console;

import me.edgrrrr.de.config.Setting;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Logger;

public enum LogLevel {
    DEBUG(ChatColor.DARK_PURPLE, Setting.CHAT_DEBUG_COLOR.path),
    INFO(ChatColor.GREEN, Setting.CHAT_INFO_COLOR.path),
    WARNING(ChatColor.YELLOW, Setting.CHAT_WARNING_COLOR.path),
    SEVERE(ChatColor.DARK_RED, Setting.CHAT_SEVERE_COLOR.path),
    MIGRATE(ChatColor.GOLD, null);

    private ChatColor colour;
    private final String colourOption;

    LogLevel(ChatColor color, String colourOption) {
        this.colour = color;
        this.colourOption = colourOption;
    }

    public static void loadValuesFromConfig(YamlConfiguration config) {
        for (LogLevel level : values()) {
            // Skip migrate level
            String settingKey = level.getColourOption();
            if (settingKey == null) {
                continue;
            }

            // Load the colour from the config
            String value = config.getString(settingKey);
            try {
                level.setColour(ChatColor.valueOf(value));
            } catch (Exception e) {
                Logger.getLogger("Minecraft").severe(String.format("Exception occurred on log level loading (%s): %s", level, e.getMessage()));
            }
        }
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
}
