package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIMaterialInflation extends DivinityExpansion {
    public PAPIMaterialInflation(DEPlugin main) {
        super(main, "^material_inflation$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%,.2f", this.getMain().getMaterialManager().getInflation());
    }
}
