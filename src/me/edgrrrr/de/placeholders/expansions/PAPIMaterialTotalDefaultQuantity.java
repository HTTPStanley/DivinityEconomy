package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialTotalDefaultQuantity extends DivinityExpansion {
    public PAPIMaterialTotalDefaultQuantity(DEPlugin main) {
        super(main, "^material_total_default_quantity$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%d", this.getMain().getMatMan().getDefaultTotalItems());
    }
}
