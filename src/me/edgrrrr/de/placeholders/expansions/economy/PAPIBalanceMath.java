package me.edgrrrr.de.placeholders.expansions.economy;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.mariuszgromada.math.mxparser.Expression;

public class PAPIBalanceMath extends DivinityExpansion {
    public PAPIBalanceMath(DEPlugin main) {
        super(main, "^(raw_|)balance_math_([+-/*])_([0-9]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        boolean formatted = value.replaceFirst(this.value, "$1").isEmpty();
        double result = new Expression(value.replaceFirst(this.value, String.format("%f$2$3", getMain().getEconMan().getBalance(player)))).calculate();
        if (formatted)
            return getMain().getConsole().formatMoney(result);
        return String.format("%,.2f", result);
    }
}
