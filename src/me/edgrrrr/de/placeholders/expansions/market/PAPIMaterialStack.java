package me.edgrrrr.de.placeholders.expansions.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialStack extends DivinityExpansion {
    public PAPIMaterialStack(DEPlugin main) {
        super(main, "^material_stack_([a-zA-Z_]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String material = value.replaceFirst(this.value, "$1");
        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(material);
        if (marketableMaterial != null) return String.format("%d", marketableMaterial.getMaterial().getMaxStackSize());
        else return LangEntry.W_empty.get(getMain());
    }
}
