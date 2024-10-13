package org.divinitycraft.divinityeconomy.placeholders.expansions.worlds;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PAPIMarketEnabled extends DivinityExpansion {
    public PAPIMarketEnabled(DEPlugin main) {
        super(main, "^market_enabled$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return "uknown";
        }
        return getMain().getWorldMan().isMarketEnabled(onlinePlayer.getWorld()) ? "Enabled" : "Disabled";
    }
}
