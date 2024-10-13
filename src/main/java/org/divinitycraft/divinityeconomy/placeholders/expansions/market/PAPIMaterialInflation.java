package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialInflation extends DivinityExpansion {
    public PAPIMaterialInflation(DEPlugin main) {
        super(main, "^material_inflation$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%,.2f", getMain().getMatMan().getInflation());
    }
}
