package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantTotalQuantity extends DivinityExpansion {
    public PAPIEnchantTotalQuantity(DEPlugin main) {
        super(main, "^enchant_total_quantity$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%d", getMain().getEnchMan().getTotalItems());
    }
}
