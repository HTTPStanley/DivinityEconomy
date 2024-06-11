package me.edgrrrr.de.placeholders.expansions.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.OfflinePlayer;

public class PAPIExpValue extends DivinityExpansion {
    public PAPIExpValue(DEPlugin main) {
        super(main, "^(raw_|)exp_(b|s)value_([0-9]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        boolean formatted = value.replaceFirst(this.value, "$1").isEmpty();
        boolean isPurchase = value.replaceFirst(this.value, "$2").equals("b");
        long amount = Converter.constrainLong(Converter.getLong(value.replaceFirst(this.value, "$3")), getMain().getExpMan().getMinTradableExp(), getMain().getExpMan().getMaxTradableExp());
        ValueResponse vr;

        if (isPurchase)
            vr = getMain().getExpMan().getBuyValue(amount);
        else
            vr = getMain().getExpMan().getSellValue(amount);


        if (vr.isFailure())
            return vr.getErrorMessage();


        if (formatted)
            return getMain().getConsole().formatMoney(vr.getValue());

        return String.format("%,.2f", vr.getValue());
    }
}
