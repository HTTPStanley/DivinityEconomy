package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.OfflinePlayer;

public class PAPIExpBValue extends DivinityExpansion {
    public PAPIExpBValue(DEPlugin main) {
        super(main, "^exp_bvalue_([0-9]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        long amount = Converter.constrainLong(Converter.getLong(value.replaceFirst(this.value, "$1")), getMain().getExpMan().getMinTradableExp(), getMain().getExpMan().getMaxTradableExp());
        ValueResponse vr = getMain().getExpMan().getBuyValue(amount);
        if (vr.isFailure()) {
            return vr.getErrorMessage();
        }
        return getMain().getConsole().formatMoney(vr.getValue());
    }
}
