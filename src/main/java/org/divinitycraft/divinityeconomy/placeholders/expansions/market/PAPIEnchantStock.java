package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.enchants.MarketableEnchant;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantStock extends DivinityExpansion {
    public PAPIEnchantStock(DEPlugin main) {
        super(main, "^enchant_stock_([a-zA-Z_]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String enchant = value.replaceFirst(this.value, "$1");
        MarketableEnchant enchantData = getMain().getEnchMan().getEnchant(enchant);
        if (enchantData != null) return String.format("%d", enchantData.getQuantity());
        else return LangEntry.W_empty.get(getMain());
    }
}
