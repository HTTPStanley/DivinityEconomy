package me.edgrrrr.de.placeholderAPI;

import me.edgrrrr.de.DEPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class DivinityExpansion {
    private final DEPlugin main;
    protected final String value;

    public DivinityExpansion(DEPlugin main, String value) {
        this.main = main;
        this.value = value;
    }

    public abstract String getResult(OfflinePlayer player, String value);

    public boolean checkValue(String value) {
        return value.matches(this.value);
    }

    protected DEPlugin getMain() {
        return this.main;
    }
}
