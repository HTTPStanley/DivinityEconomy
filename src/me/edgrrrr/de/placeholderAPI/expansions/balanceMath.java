package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.mariuszgromada.math.mxparser.Expression;

public class balanceMath extends DivinityExpansion {
    public balanceMath(DEPlugin main) {
        super(main, "^balance_math_([+-/*])_([0-9]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        return this.getMain().getConsole().formatMoney(new Expression(value.replaceFirst(this.value, String.format("%f$1$2", this.getMain().getEconomyManager().getBalance(player)))).calculate());
    }
}
