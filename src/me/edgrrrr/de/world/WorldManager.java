package me.edgrrrr.de.world;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.world.events.WorldNotification;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldManager extends DivinityModule {
    // Market enabled worlds
    private List<String> configMarketEnabledWorlds;
    private HashMap<String, Boolean> marketEnabledWorlds;
    private boolean marketEnableAllWorlds;
    private boolean marketApplyToItems;
    private boolean marketApplyToEnchants;
    private boolean marketApplyToExperience;

    // Economy enabled worlds
    private List<String> configEconomyEnabledWorlds;
    private HashMap<String, Boolean> economyEnabledWorlds;
    private boolean economyEnableAllWorlds;

    public WorldManager(DEPlugin main) {
        super(main);
    }


    @Override
    public void init() {
        // Enabled worlds for market
        configMarketEnabledWorlds = getMain().getConfig().getStringList(Setting.WORLDS_MARKET_ENABLED_WORLDS_STRINGLIST.path);
        configEconomyEnabledWorlds = getMain().getConfig().getStringList(Setting.WORLDS_ECONOMY_ENABLED_WORLDS_STRINGLIST.path);

        // Create hashmaps
        this.marketEnabledWorlds = new HashMap<>();
        this.economyEnabledWorlds = new HashMap<>();

        // Enabled all worlds
        this.marketEnableAllWorlds = getMain().getConfig().getBoolean(Setting.WORLDS_MARKET_ENABLE_ALL_WORLDS_BOOLEAN.path);
        this.economyEnableAllWorlds = getMain().getConfig().getBoolean(Setting.WORLDS_ECONOMY_ENABLE_ALL_WORLDS_BOOLEAN.path);

        // Apply to items, enchants, and experience
        this.marketApplyToItems = getMain().getConfig().getBoolean(Setting.WORLDS_MARKET_APPLY_ITEMS_BOOLEAN.path);
        this.marketApplyToEnchants = getMain().getConfig().getBoolean(Setting.WORLDS_MARKET_APPLY_ENCHANTS_BOOLEAN.path);
        this.marketApplyToExperience = getMain().getConfig().getBoolean(Setting.WORLDS_MARKET_APPLY_EXP_BOOLEAN.path);

        // Enable notification.
        boolean enableNotification = getMain().getConfig().getBoolean(Setting.WORLDS_ENABLE_NOTIFICATION_BOOLEAN.path);
        if (enableNotification) {
            getMain().getServer().getPluginManager().registerEvents(new WorldNotification(getMain(), this), getMain());
        }
    }

    @Override
    public void deinit() {

    }


    /**
     * Get the enabled worlds for the market
     * @return
     */
    public List<World> getMarketEnabledWorlds() {
        List<World> worlds = new ArrayList<>();
        for (World world : getMain().getServer().getWorlds()) {
            if (this.isMarketEnabled(world)) {
                worlds.add(world);
            }
        }
        return worlds;
    }


    /**
     * Get the disabled worlds for the market
     * @return
     */
    public List<World> getMarketDisabledWorlds() {
        List<World> worlds = new ArrayList<>();
        for (World world : getMain().getServer().getWorlds()) {
            if (!this.isMarketEnabled(world)) {
                worlds.add(world);
            }
        }
        return worlds;
    }


    /**
     * Get the enabled worlds for the economy
     * @return
     */
    public List<World> getEconomyEnabledWorlds() {
        List<World> worlds = new ArrayList<>();
        for (World world : getMain().getServer().getWorlds()) {
            if (this.isEconomyEnabled(world)) {
                worlds.add(world);
            }
        }
        return worlds;
    }


    /**
     * Get the disabled worlds for the economy
     * @return
     */
    public List<World> getEconomyDisabledWorlds() {
        List<World> worlds = new ArrayList<>();
        for (World world : getMain().getServer().getWorlds()) {
            if (!this.isEconomyEnabled(world)) {
                worlds.add(world);
            }
        }
        return worlds;
    }


    /**
     * Check if the economy is enabled in the given world
     * @param worldName
     * @return
     */
    public boolean isEconomyEnabled(String worldName) {
        for (World world : getMain().getServer().getWorlds()) {
            if (world.getName().equalsIgnoreCase(worldName)) {
                return this.isEconomyEnabled(world);
            }
        }

        return false;
    }


    /**
     * Check if the economy is enabled in the given world
     * @param world
     * @return
     */
    public boolean isEconomyEnabled(World world) {
        // Check if all worlds are enabled
        if (this.economyEnableAllWorlds) {
            return true;
        }

        // If world is not in the hashmap, cache it.
        if (!this.economyEnabledWorlds.containsKey(world.getName())) {
            this.economyEnabledWorlds.put(world.getName(), this.configEconomyEnabledWorlds.contains(world.getName()));
        }

        // Return the value
        return this.economyEnabledWorlds.get(world.getName());
    }


    /**
     * Check if the market is enabled in the given world
     * @param worldName
     * @return
     */
    public boolean isMarketEnabled(String worldName) {
        for (World world : getMain().getServer().getWorlds()) {
            if (world.getName().equalsIgnoreCase(worldName)) {
                return this.isMarketEnabled(world);
            }
        }

        return false;
    }


    /**
     * Check if the market is enabled in the given world
     * @param world
     * @return
     */
    public boolean isMarketEnabled(World world) {
        // Check if all worlds are enabled
        if (this.marketEnableAllWorlds) {
            return true;
        }

        // If world is not in the hashmap, cache it.
        if (!this.marketEnabledWorlds.containsKey(world.getName())) {
            this.marketEnabledWorlds.put(world.getName(), this.configMarketEnabledWorlds.contains(world.getName()));
        }

        // Return the value
        return this.marketEnabledWorlds.get(world.getName());
    }


    /**
     * Check if the item market is enabled in the given world
     * @param world
     * @return
     */
    public boolean isItemMarketEnabled(World world) {
        return this.marketApplyToItems && this.isMarketEnabled(world);
    }


    /**
     * Check if the enchant market is enabled in the given world
     * @param world
     * @return
     */
    public boolean isEnchantMarketEnabled(World world) {
        return this.marketApplyToEnchants && this.isMarketEnabled(world);
    }


    /**
     * Check if the experience market is enabled in the given world
     * @param world
     * @return
     */
    public boolean isExperienceMarketEnabled(World world) {
        return this.marketApplyToExperience && this.isMarketEnabled(world);
    }
}
