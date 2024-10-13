package org.divinitycraft.divinityeconomy.placeholders.expansions.worlds;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class PAPIEconomyEnabledWorlds extends DivinityExpansion {
    public PAPIEconomyEnabledWorlds(DEPlugin main) {
        super(main, "^economy_enabled_worlds$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.join(", ", this.getMain().getWorldMan().getEconomyEnabledWorlds().stream().map(World::getName).toArray(String[]::new));
    }
}
