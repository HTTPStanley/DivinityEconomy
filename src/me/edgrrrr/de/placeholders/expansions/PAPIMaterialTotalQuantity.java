package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialTotalQuantity extends DivinityExpansion {
    public PAPIMaterialTotalQuantity(DEPlugin main) {
        super(main, "^material_total_quantity$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%d", getMain().getMatMan().getTotalItems());
    }
}
