package org.divinitycraft.divinityeconomy.market.items.materials;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.MapKeys;
import org.divinitycraft.divinityeconomy.market.MarketableToken;
import org.divinitycraft.divinityeconomy.market.items.ItemManager;
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
        this.dynamicPricing = this.getConfMan().getBoolean(Setting.MARKET_MATERIALS_DYN_PRICING_BOOLEAN);
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
        new BukkitRunnable() {
            @Override
            public void run() {
                int reloaded = reloadModdedItems();
                if (reloaded > 0) getConsole().info("Reloaded %d modded materials", reloaded);
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
     * Re-attempt to resolve modded / non-vanilla materials which previously
     * failed to resolve at load time. This creates fresh MarketableMaterial
     * instances via {@link #loadItem} and replaces entries that now resolve.
     *
     * @return number of items successfully reloaded with a resolved Material
     */
    public int reloadModdedItems() {
        int reloaded = 0;
        try {
            // First, attempt to resolve any existing unresolved entries (previous behaviour)
            // Collect candidates whose Material is currently unresolved
            Set<String> candidates = new HashSet<>();
            for (String key : this.itemMap.keySet()) {
                MarketableToken token = this.itemMap.get(key);
                if (!(token instanceof MarketableMaterial)) continue;
                MarketableMaterial mat = (MarketableMaterial) token;
                if (mat.getMaterial() == null) candidates.add(key);
            }

            if (candidates.isEmpty()) {
                this.getConsole().debug("No unresolved modded materials found to reload.");
                // Continue - even if there were no unresolved entries, we still want to scan for new modded materials
            } else {
                this.getConsole().info("Found %d unresolved modded material entries to attempt reload.", candidates.size());

                for (String key : candidates) {
                    MarketableMaterial token = (MarketableMaterial) this.itemMap.get(key);
                    String materialId = token.getItemConfig().getString(MapKeys.MATERIAL_ID.key, token.getID());
                    this.getConsole().info("Attempting to reload '%s' (configured MATERIAL_ID='%s')", key, materialId);

                    MarketableMaterial newMat = (MarketableMaterial) this.loadItem(token.getID(), token.getItemConfig(), token.getDefaultItemConfig());
                    if (newMat != null && newMat.check() && newMat.getMaterial() != null) {
                        // adjust totals
                        this.defaultTotalItems -= token.getDefaultQuantity();
                        this.totalItems -= token.getQuantity();
                        this.defaultTotalItems += newMat.getDefaultQuantity();
                        this.totalItems += newMat.getQuantity();

                        ((Map) this.itemMap).put(key, newMat);
                        reloaded++;
                        this.getConsole().info("Successfully reloaded '%s' -> resolved as %s", key, newMat.getMaterial().name());
                    } else {
                        this.getConsole().warn("Failed to resolve material for '%s' (MATERIAL_ID='%s')", key, materialId);
                    }
                }
            }

            // Second, scan the current server Material registry for non-vanilla (modded) materials
            // and import any that are not yet present in the market config.
            int imported = 0;
            Set<String> discovered = new HashSet<>();
            for (Material m : Material.values()) {
                try {
                    NamespacedKey k = m.getKey();
                    if (k == null) continue;
                    String namespace = k.getNamespace();
                    if (namespace == null) continue;
                    if ("minecraft".equals(namespace)) continue; // skip vanilla

                    String nsKey = k.toString(); // modid:item
                    // avoid duplicates
                    if (discovered.contains(nsKey)) continue;
                    discovered.add(nsKey);

                    // Check if already represented in itemMap (by MATERIAL_ID or ID)
                    // OR if it already exists in the config file (user may have edited it manually)
                    boolean exists = false;
                    
                    // Check itemMap first
                    for (MarketableToken token : this.itemMap.values()) {
                        String mid = token.getItemConfig().getString(MapKeys.MATERIAL_ID.key, token.getID());
                        if (mid == null) continue;
                        if (mid.equalsIgnoreCase(nsKey) || mid.equalsIgnoreCase(m.name()) || mid.equalsIgnoreCase(k.getKey())) {
                            exists = true;
                            break;
                        }
                    }
                    
                    // Also check the config file itself to avoid overwriting user edits
                    if (!exists && this.config.contains(nsKey)) {
                        exists = true;
                    }

                    if (exists) continue;

                    // Create config section for this modded material with sensible defaults
                    this.getConsole().info("Importing modded material: %s", nsKey);
                    this.config.set(nsKey + "." + MapKeys.MATERIAL_ID.key, nsKey);
                    this.config.set(nsKey + "." + MapKeys.QUANTITY.key, 0);
                    this.config.set(nsKey + "." + MapKeys.ALLOWED.key, true);

                    // Create a minimal default section for loadItem
                    YamlConfiguration defaultSec = new YamlConfiguration();
                    defaultSec.set(MapKeys.QUANTITY.key, 0);
                    defaultSec.set(MapKeys.ALLOWED.key, true);

                    MarketableMaterial newMat = (MarketableMaterial) this.loadItem(nsKey, this.config.getConfigurationSection(nsKey), defaultSec);
                    if (newMat != null && newMat.check() && newMat.getMaterial() != null) {
                        // add into map and adjust totals
                        this.defaultTotalItems += newMat.getDefaultQuantity();
                        this.totalItems += newMat.getQuantity();
                        ((Map) this.itemMap).put(nsKey.toLowerCase().replace(" ", ""), newMat);
                        imported++;
                        this.getConsole().info("Imported modded material '%s' -> %s", nsKey, newMat.getMaterial().name());
                        
                        // Automatically create an alias using just the key part (after the colon)
                        // e.g., "cobblemon:mago_berry" -> alias "mago_berry"
                        // Alias will be saved later in batch
                        try {
                            int colonIdx = nsKey.indexOf(':');
                            if (colonIdx > 0 && colonIdx < nsKey.length() - 1) {
                                String keyPart = nsKey.substring(colonIdx + 1);
                                this.addAlias(keyPart, nsKey);
                            }
                        } catch (Exception e) {
                            this.getConsole().warn("Failed to create alias for '%s': %s", nsKey, e.getMessage());
                        }
                    } else {
                        // Rollback config if load failed
                        this.config.set(nsKey, null);
                        this.getConsole().warn("Failed to import modded material '%s'", nsKey);
                    }
                } catch (Throwable ignored) {
                }
            }

            if (imported > 0) {
                // Persist new entries
                this.saveItems();
                this.getConsole().info("Imported %d new modded materials into %s", imported, this.itemFile);
                reloaded += imported;
                
                // Save all created aliases at once
                this.saveAliases();
                this.getConsole().info("Saved aliases for imported modded materials");
            }
        } catch (Exception e) {
            this.getConsole().warn("Failed to reload modded materials: %s", e.getMessage());
        }
        return reloaded;
    }
}
