package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIRawPlayerBalance extends DivinityExpansion {
    public PAPIRawPlayerBalance(DEPlugin main) {
        super(main, "^raw_player_balance$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return String.format("%,.2f", getMain().getEconMan().getBalance(player));
    }
}
