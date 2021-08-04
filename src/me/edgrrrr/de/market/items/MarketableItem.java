package me.edgrrrr.de.market.items;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.MarketableToken;
import org.bukkit.configuration.ConfigurationSection;

public abstract class MarketableItem extends MarketableToken {

    public MarketableItem(DEPlugin main, ItemManager itemManager, String ID, ConfigurationSection config, ConfigurationSection defaultConfig) {
        super(main, itemManager, ID, config, defaultConfig);
    }

    /**
     * Returns the manager of this item
     */
    public ItemManager getManager() {
        return (ItemManager) this.tokenManager;
    }
}
