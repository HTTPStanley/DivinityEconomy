package me.edgrrrr.de.placeholders.expansions.worlds;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMarketEnabledWorld extends DivinityExpansion {
    public PAPIMarketEnabledWorld(DEPlugin main) {
        super(main, "^market_enabled_world_([a-zA-Z0-9_]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String world = value.replaceFirst(this.value, "$1");
        return this.getMain().getWorldMan().isMarketEnabled(world) ? "Enabled" : "Disabled";
    }
}
