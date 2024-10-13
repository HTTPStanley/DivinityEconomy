package org.divinitycraft.divinityeconomy.market.exp;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.market.TokenManager;
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
