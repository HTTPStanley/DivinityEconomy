package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialStock extends DivinityExpansion {
    public PAPIMaterialStock(DEPlugin main) {
        super(main, "^material_stock_([a-zA-Z_]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String material = value.replaceFirst(this.value, "$1");
        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(material);
        if (marketableMaterial != null) return String.format("%d", marketableMaterial.getQuantity());
        else return LangEntry.W_empty.get(getMain());
    }
}
