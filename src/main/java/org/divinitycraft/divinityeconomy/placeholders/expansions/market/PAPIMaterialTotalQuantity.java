package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
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
