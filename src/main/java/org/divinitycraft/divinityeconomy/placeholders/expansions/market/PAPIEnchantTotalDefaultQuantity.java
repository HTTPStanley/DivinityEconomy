package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantTotalDefaultQuantity extends DivinityExpansion {
    public PAPIEnchantTotalDefaultQuantity(DEPlugin main) {
        super(main, "^enchant_total_default_quantity$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%d", getMain().getEnchMan().getDefaultTotalItems());
    }
}
