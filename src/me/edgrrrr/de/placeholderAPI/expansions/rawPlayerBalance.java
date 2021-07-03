package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class rawPlayerBalance extends DivinityExpansion {
    public rawPlayerBalance(DEPlugin main) {
        super(main, "^raw_player_balance$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%,.2f", this.getMain().getEconomyManager().getBalance(player));
    }
}
