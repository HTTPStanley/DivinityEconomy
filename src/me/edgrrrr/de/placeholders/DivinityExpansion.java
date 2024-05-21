package me.edgrrrr.de.placeholders;

import me.edgrrrr.de.DEPlugin;
import org.bukkit.OfflinePlayer;

public abstract class DivinityExpansion {
    protected final String value;
    private final DEPlugin main;

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
