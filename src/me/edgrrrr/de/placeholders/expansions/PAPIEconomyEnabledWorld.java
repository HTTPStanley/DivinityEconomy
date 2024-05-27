package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEconomyEnabledWorld extends DivinityExpansion {
    public PAPIEconomyEnabledWorld(DEPlugin main) {
        super(main, "^economy_enabled_world_([a-zA-Z0-9 ]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String world = value.replaceFirst(this.value, "$1");
        return this.getMain().getWorldMan().isEconomyEnabled(world) ? "Enabled" : "Disabled";
    }
}
