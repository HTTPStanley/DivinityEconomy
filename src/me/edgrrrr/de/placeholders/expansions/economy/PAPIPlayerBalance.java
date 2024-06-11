package me.edgrrrr.de.placeholders.expansions.economy;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIPlayerBalance extends DivinityExpansion {
    public PAPIPlayerBalance(DEPlugin main) {
        super(main, "^(raw_|)player_balance$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        boolean formatted = value.replaceFirst(this.value, "$1").isEmpty();
        double balance = getMain().getEconMan().getBalance(player);

        if (formatted)
            return getMain().getConsole().formatMoney(balance);

        return String.format("%,.2f", balance);
    }
}
