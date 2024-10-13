package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
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
