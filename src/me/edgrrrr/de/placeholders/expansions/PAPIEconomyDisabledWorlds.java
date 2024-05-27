package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class PAPIEconomyDisabledWorlds extends DivinityExpansion {
    public PAPIEconomyDisabledWorlds(DEPlugin main) {
        super(main, "^economy_disabled_worlds$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.join(", ", this.getMain().getWorldMan().getEconomyDisabledWorlds().stream().map(World::getName).toArray(String[]::new));
    }
}
