package me.edgrrrr.de.placeholders.expansions.worlds;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class PAPIMarketEnabledWorlds extends DivinityExpansion {
    public PAPIMarketEnabledWorlds(DEPlugin main) {
        super(main, "^market_enabled_worlds$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.join(", ", this.getMain().getWorldMan().getMarketEnabledWorlds().stream().map(World::getName).toArray(String[]::new));
    }
}
