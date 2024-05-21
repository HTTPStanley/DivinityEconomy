package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIEnchantInflation extends DivinityExpansion {
    public PAPIEnchantInflation(DEPlugin main) {
        super(main, "^enchant_inflation$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%,.2f", getMain().getEnchMan().getInflation());
    }
}
