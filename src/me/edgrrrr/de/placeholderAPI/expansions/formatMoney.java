package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class formatMoney extends DivinityExpansion {
    public formatMoney(DEPlugin main) {
        super(main, "^format_money_(.*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return this.getMain().getConsole().formatMoney(Double.parseDouble(value.replaceFirst(this.value, "$1")));
    }
}
