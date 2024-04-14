package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantTotalDefaultQuantity extends DivinityExpansion {
    public PAPIEnchantTotalDefaultQuantity(DEPlugin main) {
        super(main, "^enchant_total_default_quantity$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%d", getMain().getEnchMan().getDefaultTotalItems());
    }
}
