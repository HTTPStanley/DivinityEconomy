package me.edgrrrr.de.placeholders.expansions.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.placeholders.DivinityExpansion;
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
