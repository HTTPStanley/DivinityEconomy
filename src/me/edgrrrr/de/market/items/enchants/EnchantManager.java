package me.edgrrrr.de.market.items.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.response.MultiValueResponse;
import me.edgrrrr.de.response.Response;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Enchant manager
 * Used for managing prices and quantity of enchants
 */
public class EnchantManager extends ItemManager {
    private boolean allowUnsafe;

    /**
     * Constructor
     * Note that this does not automatically load the enchants.
     * Use #loadEnchants() to read the enchants into memory.
     *
     * @param main The main class
     */
    public EnchantManager(DEPlugin main) {
        super(main, "enchants.yml", "enchantAliases.yml", new ConcurrentHashMap<String, MarketableEnchant>());
    }

    @Override
    public void init() {
        super.init();
        this.allowUnsafe = this.getConfMan().getBoolean(Setting.MARKET_ENCHANTS_ALLOW_UNSAFE_BOOLEAN);
    }

    public MarketableEnchant getEnchant(String alias) {
        return (MarketableEnchant) this.getItem(alias);
    }

    /**
     * Returns if the enchantment given is supported by the itemstack given.
     */
    public boolean supportsEnchant(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta) {
            return true;
        }

        return enchantment.canEnchantItem(itemStack);
    }

    /**
     * Returnst the type of token
     *
     * @return String
     */
    @Override
    public String getType() {
        return "ENCHANTMENT";
    }


    /**
     * Returns the number of enchants, each enchant being worth one enchant, not the stock of the enchants.
     */
    public int getEnchantCount() {
        return this.itemMap.size();
    }

    /**
     * Reduces an enchant level on an itemstack by levels amount
     * If the level is 5 and you remove 4, the level is set to 1.
     * If the level is 5 and you remove 5, the enchant is removed.
     *
     * @param itemStack   - The itemstack to remove the enchant from
     * @param enchantment - The enchantment to remove
     * @param levels      - The levels to remove
     */
    public void removeEnchantLevelsFromItem(ItemStack itemStack, Enchantment enchantment, int levels) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        // If item can store enchants
        if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            int currentLevel = enchantmentStorageMeta.getStoredEnchantLevel(enchantment);
            enchantmentStorageMeta.removeStoredEnchant(enchantment);
            int levelsLeft = currentLevel - levels;
            if (levelsLeft > 0) {
                enchantmentStorageMeta.addStoredEnchant(enchantment, levelsLeft, true);
            } else {
                if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                    itemStack.setType(Material.BOOK);
                }
            }

            itemStack.setItemMeta(enchantmentStorageMeta);
        }

        // If item itself is enchanted
        else {
            int currentLevel = itemStack.getEnchantmentLevel(enchantment);
            itemStack.removeEnchantment(enchantment);
            int levelsLeft = currentLevel - levels;
            if (levelsLeft > 0) {
                itemStack.addUnsafeEnchantment(enchantment, levelsLeft);
            }
        }
    }

    /**
     * Adds an enchant to an item
     *
     * @param itemStack   - The itemstack
     * @param enchantment - The enchant
     * @param levels      - The levels
     * @return Response
     */
    public Response addEnchantToItem(ItemStack itemStack, Enchantment enchantment, int levels) {

        // Get meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Get new desired level
        int newLevel = levels + itemStack.getEnchantmentLevel(enchantment);

        // Get enchant data from economy
        MarketableEnchant enchantData = (MarketableEnchant) this.getItem(enchantment.getKey().getKey());


        // If enchant data is null, return failure.
        if (enchantData == null) {
            return new Response(EconomyResponse.ResponseType.FAILURE, "enchant is not supported");
        }

        // If enchant level is greater than maximum, return failure.
        if (enchantData.getMaxLevel() < newLevel) {
            return new Response(EconomyResponse.ResponseType.FAILURE, String.format("level is greater than max (%d/%d)", newLevel, enchantData.getMaxLevel()));
        }


        // Add ItemMeta Stored Enchant
        if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            enchantmentStorageMeta.addStoredEnchant(enchantment, newLevel, true);
            itemStack.setItemMeta(enchantmentStorageMeta);
            if (itemStack.getType() == Material.BOOK) {
                itemStack.setType(Material.ENCHANTED_BOOK);
            }
        }

        // Add ItemStack enchant
        else {
            itemStack.addUnsafeEnchantment(enchantment, newLevel);
        }

        return new Response(EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * Returns if an item is enchanted or not.
     * Supports un-enchantable items
     *
     * @param itemStack - The itemstack to check
     * @return boolean - Is enchanted / Is not enchanted
     */
    public boolean isEnchanted(ItemStack itemStack) {

        // Get item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // If item is has enchantedment storage
        if (itemMeta instanceof EnchantmentStorageMeta) {
            return ((EnchantmentStorageMeta) itemMeta).hasStoredEnchants();
        }

        // Else return itemstack meta.
        return itemMeta.hasEnchants();
    }

    /**
     * Returns the total purchase price of all the enchants on an itemstack
     *
     * @param itemStack - The itemstack to check
     * @return MultiValueResponse
     */
    public MultiValueResponse getBulkBuyValue(ItemStack itemStack) {
        Map<String, Double> values = MultiValueResponse.createValues();
        Map<String, Integer> quantities = MultiValueResponse.createQuantities();
        EconomyResponse.ResponseType responseType = EconomyResponse.ResponseType.SUCCESS;
        String errorMessage = "";

        Map<Enchantment, Integer> enchantmentLevels = itemStack.getEnchantments();
        for (Enchantment enchantment : enchantmentLevels.keySet()) {
            int level = enchantmentLevels.get(enchantment);
            String enchantID = enchantment.getKey().getKey();
            ValueResponse valueResponse = this.getBuyValue(itemStack, enchantID, level);
            if (valueResponse.isFailure()) {
                errorMessage = valueResponse.errorMessage;
                responseType = valueResponse.responseType;
                break;
            } else {
                values.put(enchantID, valueResponse.value);
                quantities.put(enchantID, level);
            }
        }

        return new MultiValueResponse(values, quantities, responseType, errorMessage);
    }

    /**
     * Use getBulkBuyValue(ItemStack)
     *
     * @param itemStack - The item stack to get the value of
     * @return ValueResponse
     */
    @Deprecated
    @Override
    public ValueResponse getBuyValue(ItemStack itemStack, int amount) throws NullPointerException {
        return null;
    }

    /**
     * Returns the item given as base class DivinityItem is abstract and cannot be instantiated
     *
     * @param data       - The data to load
     * @param defaultData - The default data to load
     * @param ID        - The ID of the item
     * @return MarketableToken
     */
    @Override
    public MarketableToken loadItem(String ID, ConfigurationSection data, ConfigurationSection defaultData) {
        return new MarketableEnchant(this.getMain(), this, ID, data, defaultData);
    }

    /**
     * Returns the purchase value of the enchantID provided at the given level.
     *
     * @param enchantID   - The enchantment ID
     * @param levelsToBuy - The enchantment level
     * @param itemStack   - The itemStack to apply to
     * @return EnchantValueResponse - The value of the enchant
     */
    public ValueResponse getBuyValue(ItemStack itemStack, String enchantID, int levelsToBuy) {
        // Get enchant data
        MarketableEnchant enchantData = (MarketableEnchant) this.getItem(enchantID);


        // Enchant data is null
        if (enchantData == null) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("enchant id %s does not exist", enchantID));


        // Check enchant exists in store
        if (enchantData.getEnchantment() == null) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("enchant id %s does not exist in the store", enchantID));


        // Check enchant is allowed
        if (!(enchantData.getAllowed())) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "enchant is banned");


        // Check enchant is supported on item
        if (!(this.supportsEnchant(itemStack, enchantData.getEnchantment()) || this.allowUnsafe)) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "this item does not support that enchant");


        // Get current and new enchantment level
        Map<Enchantment, Integer> enchantments = EnchantManager.getEnchantments(itemStack);
        int itemStackEnchantmentLevel = enchantments.getOrDefault(enchantData.getEnchantment(), 0);
        int newTotalLevel = itemStackEnchantmentLevel + levelsToBuy;


        // Check new level isn't greater than max
        if (enchantData.getMaxLevel() < newTotalLevel) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("level would be above max(%d/%d)", newTotalLevel, enchantData.getMaxLevel()));


        // Get levels to books amount (enchant purchase count)
        int enchantAmount = MarketableEnchant.levelsToBooks(itemStackEnchantmentLevel, newTotalLevel);


        // Check store has enough
        if (enchantAmount > enchantData.getQuantity()) return  new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("not enough stock (%d/%d)", enchantAmount, enchantData.getQuantity()));


        // Get enchant price
        double price = this.calculatePrice(enchantAmount, enchantData.getQuantity(), this.buyScale, false);


        // Check market isn't saturated
        if (price <= 0) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");


        // Return success
        return  new ValueResponse(price, EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * Returns the sell value of all enchants on an itemstack
     *
     * @param itemStack - The itemstack to check
     * @return MultiEnchantValueResponse - The value of each enchant
     */
    public MultiValueResponse getBulkSellValue(ItemStack itemStack) {
        Map<String, Double> values = MultiValueResponse.createValues();
        Map<String, Integer> quantities = MultiValueResponse.createQuantities();
        EconomyResponse.ResponseType responseType = EconomyResponse.ResponseType.SUCCESS;
        String errorMessage = "";

        Map<Enchantment, Integer> itemStackEnchants = itemStack.getEnchantments();
        for (Enchantment enchantment : itemStackEnchants.keySet()) {
            int level = itemStackEnchants.get(enchantment);
            String enchantID = enchantment.getKey().getKey();
            ValueResponse valueResponse = this.getSellValue(itemStack, enchantID, level);
            if (valueResponse.isFailure()) {
                errorMessage = valueResponse.errorMessage;
                responseType = valueResponse.responseType;
                break;
            } else {
                values.put(enchantID, valueResponse.value);
                quantities.put(enchantID, level);
            }
        }

        return new MultiValueResponse(values, quantities, responseType, errorMessage);
    }

    /**
     * Use getBulkSellValue(ItemStack)
     *
     * @param itemStacks
     * @return
     */
    @Deprecated
    @Override
    public MultiValueResponse getBulkSellValue(ItemStack[] itemStacks) throws NullPointerException {
        return null;
    }

    /**
     * Returns the names and aliases for the itemstack given
     *
     * @param itemStack
     * @return
     */
    @Override
    public String[] getItemNames(ItemStack itemStack) {
        // Array list for enchants
        ArrayList<String> itemNames = new ArrayList<>();

        // Get item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Loop through enchants in storage meta
        // Add enchants to array list
        Map<Enchantment, Integer> enchantments = EnchantManager.getEnchantments(itemStack);
        for (Enchantment enchantment : enchantments.keySet()) {
            itemNames.add(enchantment.getKey().getKey());
        }

        return itemNames.toArray(new String[0]);
    }

    public String[] getCompatibleEnchants(ItemStack itemStack) {
        // Array list for enchants
        ArrayList<String> itemNames = new ArrayList<>();

        // Get item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Loop through enchants in storage meta
        // Add enchants to array list - if they are compatible with the item or allowUnsafe is true
        for (MarketableToken token : this.itemMap.values()) {
            MarketableEnchant enchant = (MarketableEnchant) token;

            // Check if addable
            boolean canAdd = false;
            if (allowUnsafe) {
                canAdd = true;
            } else if (enchant.getEnchant().canEnchantItem(itemStack)) {
                canAdd = true;
            } else if (itemMeta instanceof EnchantmentStorageMeta) {
                canAdd = true;
            }

            // Add if you can add
            if (canAdd) {
                itemNames.addAll(Arrays.asList(this.revAliasMap.get((enchant.getID().toLowerCase()))));
            }
        }
        return itemNames.toArray(new String[0]);
    }

    public String[] getCompatibleEnchants(ItemStack itemStack, String startsWith) {
        return this.searchItemNames(this.getCompatibleEnchants(itemStack), startsWith);
    }

    /**
     * Returns the names and aliases for the itemstack given starting with startswith
     *
     * @param itemStack
     * @param startswith
     * @return
     */
    @Override
    public String[] getItemNames(ItemStack itemStack, String startswith) {
        return this.searchItemNames(this.getItemNames(itemStack), startswith);
    }

    /**
     * Use getSellValue(ItemStack, String, int)
     *
     * @param itemStack - The itemStack to get the value of
     * @return
     */
    @Deprecated
    @Override
    public ValueResponse getSellValue(ItemStack itemStack, int amount) throws NullPointerException {
        return null;
    }

    /**
     * Returns the value of an enchant on an item.
     *
     * @param itemStack    - The itemstack to check
     * @param enchantID    - The enchant ID to check for
     * @param levelsToSell - The level to value
     * @return ValueResponse
     */
    public ValueResponse getSellValue(ItemStack itemStack, String enchantID, int levelsToSell) {
        MarketableEnchant enchantData = (MarketableEnchant) this.getItem(enchantID);

        // No enchant data
        if (enchantData == null) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("unknown enchant id %s", enchantID));


        // Get enchantment
        Enchantment enchantment = enchantData.getEnchantment();


        // No enchantment
        if (enchantment == null) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("unknown enchant id %s", enchantID));


        // Check enchant is allowed
        if (!enchantData.getAllowed()) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "enchant is banned");


        // Get itemstack enchantments
        Map<Enchantment, Integer> itemStackEnchants = EnchantManager.getEnchantments(itemStack);


        // Check stored enchants contain given enchant
        if (!itemStackEnchants.containsKey(enchantment)) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item does not have enchant %s", enchantID));


        // Get enchantment stack level
        int itemStackEnchantLevel = itemStackEnchants.get(enchantment);


        // Check enough levels to sell
        if (itemStackEnchantLevel < levelsToSell) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item enchant does not have enough levels(%d/%d)", itemStackEnchantLevel, levelsToSell));


        // Get value
        double value = this.calculatePrice(MarketableEnchant.levelsToBooks(itemStackEnchantLevel, itemStackEnchantLevel - levelsToSell), enchantData.getQuantity(), this.sellScale, false);


        // Value equal or less than 0, return saturation
        if (value <= 0) return new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");


        return new ValueResponse(value, EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * Edits the quantity of an enchant & the total quantity of enchants
     *
     * @param enchantData - The enchant to edit
     * @param levels      - The quantity to edit by, in levels. Can be negative.
     */
    public void editLevelQuantity(MarketableEnchant enchantData, int levels) {
        int books;
        if (levels > 0) {
            books = MarketableEnchant.levelsToBooks(0, levels);
        } else {
            books = -MarketableEnchant.levelsToBooks(0, -levels);
        }
        this.editQuantity(enchantData, books);
    }



    public static Map<Enchantment, Integer> getEnchantments(ItemStack itemStack) {
        // Get item stack meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Item can store enchants, return stored enchants
        if (itemMeta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;
            return enchantmentStorageMeta.getStoredEnchants();
        }

        // Return direct enchants
        return itemMeta.getEnchants();
    }
}
