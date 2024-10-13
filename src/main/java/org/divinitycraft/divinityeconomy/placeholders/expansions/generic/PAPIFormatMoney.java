package org.divinitycraft.divinityeconomy.placeholders.expansions.generic;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIFormatMoney extends DivinityExpansion {
    public PAPIFormatMoney(DEPlugin main) {
        super(main, "^format_money_([a-zA-Z]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return getMain().getConsole().formatMoney(Double.parseDouble(value.replaceFirst(this.value, "$1")));
    }
}
