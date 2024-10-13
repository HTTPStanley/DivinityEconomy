package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialValue extends DivinityExpansion {
    public PAPIMaterialValue(DEPlugin main) {
        super(main, "^(raw_|)material_(s|b)value_([a-zA-Z_]*)_([0-9]*)$");
    }

    public String getResult(OfflinePlayer player, String value) {
        boolean formatted = value.replaceFirst(this.value, "$1").isEmpty();
        boolean isPurchase = value.replaceFirst(this.value, "$2").equals("b");
        String material = value.replaceFirst(this.value, "$3");
        int amount = Converter.getInt(value.replaceFirst(this.value, "$4"));
        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(material);
        if (marketableMaterial != null) {
            double price;
            if (isPurchase)
                price = marketableMaterial.getManager().getBuyValue(marketableMaterial.getItemStacks(amount)).getValue();
            else
                price = marketableMaterial.getManager().getSellValue(marketableMaterial.getItemStacks(amount)).getValue();


            if (formatted)
                return getMain().getConsole().formatMoney(price);
            else
                return String.format("%.2f", price);
        }
        else return LangEntry.W_empty.get(getMain());
    }
}
