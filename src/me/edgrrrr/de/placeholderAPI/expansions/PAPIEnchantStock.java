package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.enchants.EnchantData;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantStock extends DivinityExpansion {
    public PAPIEnchantStock(DEPlugin main) {
        super(main, "^enchant_stock_(.*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String enchant = value.replaceFirst(this.value, "$1");
        EnchantData enchantData = this.getMain().getEnchantmentManager().getEnchant(enchant);
        if (enchantData != null) return String.format("%d", enchantData.getQuantity());
        else return String.format("Unknown enchant '%s'", enchant);
    }
}
