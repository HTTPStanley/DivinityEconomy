package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialTotalDefaultQuantity extends DivinityExpansion {
    public PAPIMaterialTotalDefaultQuantity(DEPlugin main) {
        super(main, "^material_total_default_quantity$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%d", getMain().getMatMan().getDefaultTotalItems());
    }
}
