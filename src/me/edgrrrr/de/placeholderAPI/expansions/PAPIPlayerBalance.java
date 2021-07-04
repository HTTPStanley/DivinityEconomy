package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIPlayerBalance extends DivinityExpansion {
    public PAPIPlayerBalance(DEPlugin main) {
        super(main, "^player_balance$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return this.getMain().getConsole().getFormattedBalance(player);
    }
}
