package me.edgrrrr.de.market.items.enchants;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.items.ItemManager;
import me.edgrrrr.de.response.MultiValueResponse;
import me.edgrrrr.de.response.Response;
import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

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
     * Removes the given enchants and levels from the itemStack given.
     *
     * @param itemStack            - The itemstack to remove the enchants from
     * @param enchantmentAndLevels - The enchantments and the level to remove
     */
    public void removeEnchantLevelsFromItem(ItemStack itemStack, ConcurrentHashMap<Enchantment, Integer> enchantmentAndLevels) {
        for (Enchantment enchantment : enchantmentAndLevels.keySet()) {
            this.removeEnchantLevelsFromItem(itemStack, enchantment, enchantmentAndLevels.get(enchantment));
        }
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
        int currentLevel = itemStack.getEnchantmentLevel(enchantment);
        itemStack.removeEnchantment(enchantment);
        int levelsLeft = currentLevel - levels;
        if (levelsLeft > 0) {
            itemStack.addUnsafeEnchantment(enchantment, levelsLeft);
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
        Response response;
        int newLevel = levels + itemStack.getEnchantmentLevel(enchantment);
        MarketableEnchant enchantData = (MarketableEnchant) this.getItem(enchantment.getKey().getKey());
        if (enchantData == null) {
            response = new Response(EconomyResponse.ResponseType.FAILURE, "enchant is not supported");
        } else {
            if (enchantData.getMaxLevel() < newLevel) {
                response = new Response(EconomyResponse.ResponseType.FAILURE, String.format("level is greater than max (%d/%d)", newLevel, enchantData.getMaxLevel()));
            } else {
                itemStack.addUnsafeEnchantment(enchantment, newLevel);
                response = new Response(EconomyResponse.ResponseType.SUCCESS, "");
            }
        }

        return response;
    }

    /**
     * Returns if an item is enchanted or not.
     * Supports un-enchantable items
     *
     * @param itemStack - The itemstack to check
     * @return boolean - Is enchanted / Is not enchanted
     */
    public boolean isEnchanted(ItemStack itemStack) {
        return itemStack.getEnchantments().size() >= 1;
    }

    /**
     * Returns A hashmap of the enchantdata, level
     * If there are no enchants, the hashmap will be empty.
     * Supports unenchantable items
     *
     * @param itemStack - The itemstack to check.
     * @return HashMap<EnchantData, Integer> - The Enchants and their respective level
     */
    public Map<MarketableEnchant, Integer> getEnchantLevels(ItemStack itemStack) {
        Map<MarketableEnchant, Integer> enchants = new ConcurrentHashMap<>();
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            enchants.put((MarketableEnchant) this.getItem(enchantment.getKey().getKey()), itemStack.getEnchantmentLevel(enchantment));
        }
        return enchants;
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
     * @return
     */
    @Deprecated
    @Override
    public ValueResponse getBuyValue(ItemStack itemStack, int amount) throws NullPointerException {
        return null;
    }

    /**
     * Returns the item given as base class DivinityItem is abstract and cannot be instantiated
     *
     * @param data
     * @param defaultData
     * @param ID
     * @return
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
        MarketableEnchant enchantData = (MarketableEnchant) this.getItem(enchantID);
        ValueResponse response;
        if (enchantData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("enchant id %s does not exist", enchantID));

        } else {
            if (enchantData.getEnchantment() == null) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("enchant id %s does not exist in the store", enchantID));

            } else {
                if (!(enchantData.getAllowed())) {
                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "enchant is banned");

                } else {
                    if (!(this.allowUnsafe || (this.supportsEnchant(itemStack, enchantData.getEnchantment()) && !this.allowUnsafe))) {
                        response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "this item does not support that enchant");
                    } else {
                        int itemStackEnchantmentLevel = itemStack.getEnchantmentLevel(enchantData.getEnchantment());
                        int newTotalLevel = itemStackEnchantmentLevel + levelsToBuy;
                        if (enchantData.getMaxLevel() < newTotalLevel) {
                            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("level would be above max(%d/%d)", newTotalLevel, enchantData.getMaxLevel()));

                        } else {
                            int enchantAmount = MarketableEnchant.levelsToBooks(itemStackEnchantmentLevel, newTotalLevel);
                            if (enchantAmount > enchantData.getQuantity()) {
                                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("not enough stock (%d/%d)", enchantAmount, enchantData.getQuantity()));

                            } else {
                                double price = this.calculatePrice(enchantAmount, enchantData.getQuantity(), this.buyScale, false);
                                if (price > 0) {
                                    response = new ValueResponse(price, EconomyResponse.ResponseType.SUCCESS, "");
                                } else {
                                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");
                                }
                            }
                        }
                    }
                }
            }
        }
        return response;
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
        ArrayList<String> itemNames = new ArrayList<>();
        this.itemMap.values().stream().filter(enchant -> itemStack.containsEnchantment(((MarketableEnchant) enchant).getEnchant())).forEach(enchant -> itemNames.addAll(Arrays.asList(this.revAliasMap.get((enchant.getID().toLowerCase())))));
        return itemNames.toArray(new String[0]);
    }

    public String[] getCompatibleEnchants(ItemStack itemStack) {
        ArrayList<String> itemNames = new ArrayList<>();
        this.itemMap.values().stream().filter(enchant -> ((MarketableEnchant) enchant).getEnchant().canEnchantItem(itemStack) || allowUnsafe).forEach(enchant -> itemNames.addAll(Arrays.asList(this.revAliasMap.get((enchant.getID().toLowerCase())))));
        return itemNames.toArray(new String[0]);
    }

    public String[] getCompatibleEnchants(ItemStack itemStack, String startsWith) {
        return this.filterItemNames(this.getCompatibleEnchants(itemStack), startsWith);
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
        return this.filterItemNames(this.getItemNames(itemStack), startswith);
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
        ValueResponse response;
        if (enchantData == null) {
            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("unknown enchant id %s", enchantID));
        } else {

            Enchantment enchantment = enchantData.getEnchantment();
            if (enchantment == null) {
                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("unknown enchant id %s", enchantID));

            } else {

                if (!enchantData.getAllowed()) {
                    response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "enchant is banned");

                } else {

                    Map<Enchantment, Integer> itemStackEnchants = itemStack.getEnchantments();
                    if (!itemStackEnchants.containsKey(enchantment)) {
                        response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item does not have enchant %s", enchantID));

                    } else {

                        int itemStackEnchantLevel = itemStackEnchants.get(enchantment);
                        if (itemStackEnchantLevel < levelsToSell) {
                            response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, String.format("item enchant does not have enough levels(%d/%d)", itemStackEnchantLevel, levelsToSell));
                        } else {
                            double value = this.calculatePrice(MarketableEnchant.levelsToBooks(itemStackEnchantLevel, itemStackEnchantLevel - levelsToSell), enchantData.getQuantity(), this.sellScale, false);
                            if (value > 0) {
                                response = new ValueResponse(value, EconomyResponse.ResponseType.SUCCESS, "");
                            } else {
                                response = new ValueResponse(0.0, EconomyResponse.ResponseType.FAILURE, "market is saturated.");
                            }
                        }
                    }
                }
            }
        }
        return response;
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
}
