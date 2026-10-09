package org.divinitycraft.divinityeconomy.market.items.materials;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.MapKeys;
import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.market.items.ItemManager;
import org.divinitycraft.divinityeconomy.utils.ConfigKeys;
import org.divinitycraft.divinityeconomy.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class MaterialManager extends ItemManager {

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main      - The plugin
     * @param itemFile
     * @param aliasFile
     * @param itemMap
     */
    public MaterialManager(DEPlugin main, String itemFile, String aliasFile, Map<String, ? extends MarketableMaterial> itemMap) {
        super(main, itemFile, aliasFile, itemMap);
    }

    @Override
    public void init() {
        this.saveMessagesDisabled = this.getConfMan().getBoolean(Setting.IGNORE_SAVE_MESSAGE_BOOLEAN);
        this.buyScale = this.getConfMan().getDouble(Setting.MARKET_MATERIALS_BUY_TAX_FLOAT);
        this.sellScale = this.getConfMan().getDouble(Setting.MARKET_MATERIALS_SELL_TAX_FLOAT);
        this.baseQuantity = this.getConfMan().getInt(Setting.MARKET_MATERIALS_BASE_QUANTITY_INTEGER);
        this.wholeMarketInflation = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_WHOLE_MARKET_INF_BOOLEAN);
        this.maxItemValue = this.getConfMan().getDouble(Setting.MARKET_MAX_ITEM_VALUE_DOUBLE);
        this.ignoreNamedItems = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_IGNORE_NAMED_ITEMS_BOOLEAN);
        if (this.maxItemValue < 0) {
            this.maxItemValue = Double.MAX_VALUE;
        }
        this.minItemValue = this.getConfMan().getDouble(Setting.MARKET_MIN_ITEM_VALUE_DOUBLE);
        if (this.minItemValue < 0) {
            this.minItemValue = Double.MIN_VALUE;
        }

        // Initialize pricing model
        String pricingModelName = this.getConfMan().getString(Setting.MARKET_MATERIALS_PRICING_MODEL_STRING);
        this.initializePricingModel(pricingModelName);

        int timer = Converter.getTicks(this.getConfMan().getInt(Setting.MARKET_SAVE_TIMER_INTEGER));
        this.saveTimer = new BukkitRunnable() {
            @Override
            public void run() {
                saveItems();
            }
        };
        this.saveTimer.runTaskTimerAsynchronously(getMain(), timer, timer);
        this.loadItems();
        this.loadAliases();
        // Schedule a delayed attempt to resolve modded (non-vanilla) materials
        // Some hybrid servers (NeoForge/Arclight) may register modded materials
        // after plugin enable; this will re-attempt resolution shortly after.
        if (this.supportsModdedItems()) new BukkitRunnable() {
            @Override
            public void run() {
                int reloaded = reloadModdedItems();
                if (reloaded > 0) getConsole().info(LangEntry.MODDED_Reloaded.get(getMain()), reloaded);
            }
        }.runTaskLater(getMain(), 200L); // ~10 seconds
        // this.checkLoadedItems(); - This is for internal debugging only. Hi! :)
        this.getMarkMan().addManager(this);
    }

    @Override
    public void deinit() {
        this.saveTimer.cancel();
        this.saveItems();
        this.getMarkMan().removeManager(this);
    }

    /**
     * Returns the names and aliases for the itemstack given
     *
     * @param itemStack
     * @return
     */
    @Override
    public Set<String> getItemNames(ItemStack itemStack) {
        return this.getItemNames(this.getItem(itemStack).getID());
    }

    /**
     * Returns the names and aliases for the itemstack given starting with startswith
     *
     * @param itemStack
     * @param startswith
     * @return
     */
    @Override
    public Set<String> getItemNames(ItemStack itemStack, String startswith) {
        return this.searchItemNames(this.getItemNames(itemStack), startswith);
    }

    /**
     * Returns an item from the item HashMap, Will be none if no alias or
     * direct name is found.
     *
     * @param alias - The alias or name of the item to get.
     * @return ? extends DivinityItem - Returns the material data corresponding to the string supplied.
     */
    @Override
    public MarketableMaterial getItem(String alias) {
        return (MarketableMaterial) super.getItem(alias);
    }

    /**
     * Returns the DivinityMaterial for the itemstack given
     *
     * @param itemStack - The itemstack to get
     * @return ? extends DivinityMaterial
     */
    public MarketableMaterial getItem(ItemStack itemStack) {
        for (MarketableToken thisMat : this.itemMap.values()) {
            MarketableMaterial genMat = (MarketableMaterial) thisMat;
            if (genMat.equals(itemStack)) return genMat;
        }

        return null;
    }


    @Override
    public MaterialValueResponse getSellValue(ItemStack[] itemStacks) {
        // If no items, return 0
        if (itemStacks.length == 0) {
            return new MaterialValueResponse(EconomyResponse.ResponseType.FAILURE, LangEntry.MARKET_NoItemsToSell.get(getMain()));
        }


        // Create a variable to hold the total value
        MaterialValueResponse response = new MaterialValueResponse(EconomyResponse.ResponseType.SUCCESS, null);

        // Loop through items and add up the sell value of each item
        for (ItemStack itemStack : itemStacks) {
            // Get the sell value of the item
            MaterialValueResponse thisResponse = (MaterialValueResponse) this.getSellValue(itemStack, itemStack.getAmount());
            if (thisResponse.isFailure()) continue;
            response.addResponse(thisResponse);
        }


        // Return the value
        return response;
    }


    @Override
    public MaterialValueResponse getBuyValue(ItemStack[] itemStacks) {
        // If no items, return 0
        if (itemStacks.length == 0) {
            return new MaterialValueResponse(EconomyResponse.ResponseType.FAILURE, LangEntry.MARKET_NoItemsToBuy.get(getMain()));
        }

        // Create response
        MaterialValueResponse response = new MaterialValueResponse(EconomyResponse.ResponseType.SUCCESS, null);

        for (ItemStack itemStack : itemStacks) {
            MaterialValueResponse thisResponse = (MaterialValueResponse) this.getBuyValue(itemStack, itemStack.getAmount());

            if (thisResponse.isFailure()) continue;

            // Get the buy value of the item and Add the value to the total
            response.addResponse(thisResponse);
        }

        // Return the value
        return response;
    }


    /**
     * Runs various checks on the loaded items
     */
    public void checkLoadedItems() {
        // Loop through local keys and check if they are missing from the config
        for (String key : this.getLocalKeys()) {
            if (!this.config.contains(key)) {
                this.getConsole().warn("Item '%s' is missing from the config, consider adding this item to the market.", key);
            }
        }
    }


    /**
     * Returns the local keys for this version of the market
     *
     * @return Set<String>
     */
    public Set<String> getLocalKeys() {
        return new HashSet<>();
    }

    /**
     * Whether this manager imports modded materials.
     * The scan walks Bukkit's Material registry, so only the manager that represents plain materials (blocks/items)
     * should opt in; potions and entities are keyed by PotionType/EntityType and can't be built from a Material.
     *
     * @return false by default
     */
    public boolean supportsModdedItems() {
        return false;
    }

    /**
     * Resolves and imports modded (non-vanilla) materials.
     * <p>
     * Hybrid servers (Arclight/NeoForge) can register modded materials after plugins enable, so this is run once
     * shortly after startup and on demand via /modded. It:
     * <ol>
     *     <li>loads entries already in the materials file that were skipped because their material wasn't registered yet</li>
     *     <li>imports registered modded materials that have no entry yet, as disallowed with 0 quantity</li>
     * </ol>
     * Only entries whose material resolves are ever added to the market.
     *
     * @return number of modded materials newly resolved or imported
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int reloadModdedItems() {
        if (!this.supportsModdedItems()) return 0;
        int resolved = 0;
        int imported = 0;
        try {
            // Step 1 - entries that exist in the file but aren't loaded yet (material wasn't registered at load time)
            // Bundled (vanilla) keys are skipped: they were deliberately rejected by loadItems()
            FileConfiguration bundled = this.getConfMan().readResource(this.itemFile);
            for (String key : new HashSet<>(this.config.getKeys(false))) {
                if (bundled.contains(key)) continue;
                String formattedKey = key.toLowerCase().replace(" ", "");
                if (this.itemMap.containsKey(formattedKey)) continue;

                ConfigurationSection data = this.config.getConfigurationSection(key);
                if (data == null) continue;

                try {
                    MarketableToken token = this.loadItem(key, data, data);
                    if (token == null || !token.check()) continue;

                    this.defaultTotalItems += token.getDefaultQuantity();
                    this.totalItems += token.getQuantity();
                    ((Map) this.itemMap).put(formattedKey, token);
                    resolved++;
                    this.getConsole().info(LangEntry.MODDED_Resolved.get(getMain()), key, this.describe(token));
                } catch (Exception e) {
                    this.getConsole().warn(LangEntry.MODDED_ImportFailed.get(getMain()), key, e.getMessage());
                }
            }

            // Step 2 - registered modded materials with no entry yet
            Set<String> seen = new HashSet<>();
            for (Material material : Material.values()) {
                String nsKey = null;
                try {
                    NamespacedKey namespacedKey = material.getKey();
                    if (namespacedKey == null || "minecraft".equals(namespacedKey.getNamespace())) continue;

                    nsKey = namespacedKey.toString(); // modid:item
                    if (!seen.add(nsKey)) continue;

                    // '.' is a config path separator, so ids containing one are stored under an encoded key
                    // (the real id is kept in the entry's material field)
                    String configKey = ConfigKeys.safe(nsKey);
                    String formattedKey = configKey.toLowerCase().replace(" ", "");
                    if (this.itemMap.containsKey(formattedKey)) continue;
                    if (this.config.contains(configKey)) {
                        // Already imported, unless a different id encodes to the same key
                        String existingId = this.config.getString(configKey + "." + MapKeys.MATERIAL_ID.key);
                        if (existingId != null && !existingId.equalsIgnoreCase(nsKey)) {
                            this.getConsole().warn(LangEntry.MODDED_ImportFailed.get(getMain()), nsKey, configKey + " -> " + existingId);
                        }
                        continue;
                    }

                    this.getConsole().info(LangEntry.MODDED_Importing.get(getMain()), nsKey);

                    // Disallowed with no stock until an admin sets it up
                    this.config.set(configKey + "." + MapKeys.MATERIAL_ID.key, nsKey);
                    this.config.set(configKey + "." + MapKeys.QUANTITY.key, 0);
                    this.config.set(configKey + "." + MapKeys.ALLOWED.key, false);

                    YamlConfiguration defaultSection = new YamlConfiguration();
                    defaultSection.set(MapKeys.QUANTITY.key, 0);
                    defaultSection.set(MapKeys.ALLOWED.key, false);

                    MarketableToken token = this.loadItem(configKey, this.config.getConfigurationSection(configKey), defaultSection);
                    if (token == null || !token.check()) {
                        this.config.set(configKey, null);
                        this.getConsole().warn(LangEntry.MODDED_ImportFailed.get(getMain()), nsKey, token == null ? "null" : token.getError());
                        continue;
                    }

                    this.defaultTotalItems += token.getDefaultQuantity();
                    this.totalItems += token.getQuantity();
                    ((Map) this.itemMap).put(formattedKey, token);
                    imported++;
                    this.getConsole().info(LangEntry.MODDED_Imported.get(getMain()), nsKey, this.describe(token));

                    // Alias "modid:item" -> "item" (skipped if it would shadow something else)
                    int colonIdx = nsKey.indexOf(':');
                    if (colonIdx > 0 && colonIdx < nsKey.length() - 1) {
                        this.addAlias(nsKey.substring(colonIdx + 1), configKey);
                    }
                } catch (Throwable e) {
                    if (nsKey != null) this.config.set(ConfigKeys.safe(nsKey), null);
                    this.getConsole().warn(LangEntry.MODDED_ImportFailed.get(getMain()), nsKey, e.toString());
                }
            }

            if (imported > 0) {
                this.saveItems();
                this.getConsole().info(LangEntry.MODDED_ImportedTotal.get(getMain()), imported, this.itemFile);
                this.saveAliases();
                this.getConsole().info(LangEntry.MODDED_AliasesSaved.get(getMain()));
            }
        } catch (Exception e) {
            this.getConsole().warn(LangEntry.MODDED_ReloadFailed.get(getMain()), e.toString());
        }
        return resolved + imported;
    }

    private String describe(MarketableToken token) {
        return token instanceof MarketableMaterial && ((MarketableMaterial) token).getMaterial() != null
                ? ((MarketableMaterial) token).getMaterial().name()
                : token.getID();
    }
}