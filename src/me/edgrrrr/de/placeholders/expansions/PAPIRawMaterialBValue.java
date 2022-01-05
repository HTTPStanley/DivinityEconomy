package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.OfflinePlayer;

public class PAPIRawMaterialBValue extends DivinityExpansion {
    public PAPIRawMaterialBValue(DEPlugin main) {
        super(main, "^raw_material_bvalue_(.*)_([0-9]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String material = value.replaceFirst(this.value, "$1");
        int amount = Converter.getInt(value.replaceFirst(this.value, "$2"));
        MarketableMaterial marketableMaterial = this.getMain().getMarkMan().getItem(material);
        if (marketableMaterial != null)
            return String.format("%,.2f", marketableMaterial.getManager().getBuyValue(marketableMaterial.getItemStack(amount), amount).value);
        else return String.format("Unknown material '%s'", material);
    }
}
