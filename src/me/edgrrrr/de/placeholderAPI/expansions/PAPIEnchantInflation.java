package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantInflation extends DivinityExpansion {
    public PAPIEnchantInflation(DEPlugin main) {
        super(main, "^enchant_inflation$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%,.2f", this.getMain().getEnchantmentManager().getInflation());
    }
}
