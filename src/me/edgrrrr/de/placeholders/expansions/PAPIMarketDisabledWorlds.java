package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class PAPIMarketDisabledWorlds extends DivinityExpansion {
    public PAPIMarketDisabledWorlds(DEPlugin main) {
        super(main, "^market_disabled_worlds$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.join(", ", this.getMain().getWorldMan().getMarketDisabledWorlds().stream().map(World::getName).toArray(String[]::new));
    }
}
