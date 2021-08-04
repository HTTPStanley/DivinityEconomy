package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialStock extends DivinityExpansion {
    public PAPIMaterialStock(DEPlugin main) {
        super(main, "^material_stock_(.*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String material = value.replaceFirst(this.value, "$1");
        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(material);
        if (marketableMaterial != null) return String.format("%d", marketableMaterial.getQuantity());
        else return String.format("Unknown material '%s'", material);
    }
}
