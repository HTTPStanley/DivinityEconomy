package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantInflation extends DivinityExpansion {
    public PAPIEnchantInflation(DEPlugin main) {
        super(main, "^enchant_inflation$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%,.2f", getMain().getEnchMan().getInflation());
    }
}
