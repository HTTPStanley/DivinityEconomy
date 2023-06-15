package me.edgrrrr.de.market.exp;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.TokenManager;
import org.bukkit.configuration.ConfigurationSection;

public class Exp extends MarketableToken {
    public Exp(DEPlugin main, TokenManager tokenManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, tokenManager, ID, config, defaultConfig);
    }

    public int addQuantity(int quantity) {
        assert quantity > -1;
        this.editQuantity(quantity);
        return this.getQuantity();
    }

    public int remQuantity(int quantity) {
        assert quantity > -1;
        this.editQuantity(-quantity);
        return this.getQuantity();
    }

    @Override
    public boolean check() {
        return this.getID().equals("EXPERIENCE");
    }
}
