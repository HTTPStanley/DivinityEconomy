package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.mariuszgromada.math.mxparser.Expression;

public class PAPIBalanceMath extends DivinityExpansion {
    public PAPIBalanceMath(DEPlugin main) {
        super(main, "^balance_math_([+-/*])_([0-9]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return getMain().getConsole().formatMoney(new Expression(value.replaceFirst(this.value, String.format("%f$1$2", getMain().getEconMan().getBalance(player)))).calculate());
    }
}
