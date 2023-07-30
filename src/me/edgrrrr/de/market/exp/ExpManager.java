package me.edgrrrr.de.market.exp;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.market.MarketableToken;
import me.edgrrrr.de.market.TokenManager;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

public class ExpManager extends TokenManager {
    private final int maxTradableExp = 100000;
    private final int minTradableExp = 1;

    /**
     * Constructor You will likely need to call loadMaterials and loadAliases to
     * populate the aliases and items with data from the program
     *
     * @param main - The plugin
     */
    public ExpManager(DEPlugin main) {
        super(main, "experience.yml", null, new ConcurrentHashMap<String, Exp>());
    }

    @Override
    public void init() {
        this.saveMessagesDisabled = this.getConfMan().getBoolean(Setting.IGNORE_SAVE_MESSAGE_BOOLEAN);
        this.buyScale = this.getConfMan().getDouble(Setting.MARKET_EXP_BUY_TAX_FLOAT);
        this.sellScale = this.getConfMan().getDouble(Setting.MARKET_EXP_SELL_TAX_FLOAT);
        this.baseQuantity = this.getConfMan().getInt(Setting.MARKET_EXP_BASE_QUANTITY_INTEGER);
        this.dynamicPricing = this.getConfMan().getBoolean(Setting.MARKET_EXP_DYN_PRICING_BOOLEAN);
        this.wholeMarketInflation = this.getConfMan().getBoolean(Setting.MARKET_EXP_WHOLE_MARKET_INF_BOOLEAN);
        this.maxItemValue = this.getConfMan().getDouble(Setting.MARKET_MAX_ITEM_VALUE_DOUBLE);
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
        this.saveTimer.runTaskTimerAsynchronously(this.getMain(), timer, timer);
        this.loadItems();
        this.loadAliases();
    }

    @Override
    public void deinit() {
        this.saveTimer.cancel();
        this.saveItems();
    }

    public int getMaxTradableExp() {
        return this.maxTradableExp;
    }

    public int getMinTradableExp() {
        return this.minTradableExp;
    }

    @Override
    public String getType() {
        return "EXPERIENCE";
    }


    /**
     * Returns the buy value of the experience
     * @param amount - The amount of experience to buy
     * @return - The value response
     */
    public ValueResponse getBuyValue(long amount) {
        // Check that the experience is valid
        if (amount <= 0) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "invalid experience value");
        }

        Exp exp = this.getExperience();
        // Check that experience is not banned
        if (!exp.getAllowed()) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "experience is banned");
        }

        // Check that the requested amount of experience is available to buy
        if (amount > exp.getQuantity()) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "not enough experience to buy");
        }

        // Calculate the value of the experience
        double price = this.calculatePrice(amount, exp.getQuantity(), this.buyScale, true);
        if (price < 0) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "market is saturated");
        }

        return new ValueResponse(price, EconomyResponse.ResponseType.SUCCESS, "success");
    }


    /**
     * Returns the buy value of the experience as a formatted string
     * @param amount - The amount of experience to buy
     * @return - The value response
     */
    public String getBuyValueString(long amount) {
        ValueResponse response = this.getBuyValue(amount);
        if (response.isFailure()) {
            return String.format("Error: %s", response.getErrorMessage());
        }

        return String.format("Value: %s", this.getConsole().formatMoney(response.getValue()));
    }


    public ValueResponse getSellValue(long amount, Player player) {
        // Check that the player has enough experience to sell
        if (amount > getPlayerExp(player)) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "not enough experience to sell");
        }

        return this.getSellValue(amount);
    }


    /**
     * Returns the sell value of the experience
     * @param amount - The amount of experience to sell
     * @return - The value response
     */
    public ValueResponse getSellValue(long amount) {
        // Check that the experience is valid
        if (amount <= 0) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "invalid experience value");
        }

        Exp exp = this.getExperience();
        // Check that experience is not banned
        if (!exp.getAllowed()) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "experience is banned");
        }

        // Check that the requested amount of experience is available to sell
        double price = this.calculatePrice(amount, exp.getQuantity(), this.sellScale, false);
        if (price < 0) {
            return new ValueResponse(0, EconomyResponse.ResponseType.FAILURE, "market is saturated");
        }

        return new ValueResponse(price, EconomyResponse.ResponseType.SUCCESS, "success");
    }


    public String getSellValueString(long amount, Player player) {
        ValueResponse response = this.getSellValue(amount, player);
        if (response.isFailure()) {
            return String.format("Error: %s", response.getErrorMessage());
        }

        return String.format("Value: %s", this.getConsole().formatMoney(response.getValue()));
    }

    public int addExperience(Player player, int amount) {
        // get experience
        Exp exp = this.getExperience();
        if (exp == null) {
            return 0;
        }

        exp.remQuantity(amount);
        return changePlayerExp(player, amount);
    }


    public int remExperience(Player player, int amount) {
        // get experience
        Exp exp = this.getExperience();
        if (exp == null) {
            return 0;
        }

        exp.addQuantity(amount);
        return changePlayerExp(player, -amount);
    }



    public Exp getExperience() {
        return (Exp) this.getItem("experience");
    }

    @Override
    public void loadAliases() {
    }

    @Override
    public MarketableToken loadItem(String ID, ConfigurationSection data, ConfigurationSection defaultData) {
        return new Exp(this.getMain(), this, ID, data, defaultData);
    }

    // Calculate amount of EXP needed to level up
    public static int getExpToLevelUp(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    // Calculate total experience up to a level
    public static int getExpAtLevel(int level){
        if(level <= 16){
            return (int) (Math.pow(level,2) + 6*level);
        } else if(level <= 31){
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }

    // Calculate player's current EXP amount
    public static int getPlayerExp(Player player){
        int exp = 0;
        int level = player.getLevel();

        // Get the amount of XP in past levels
        exp += getExpAtLevel(level);

        // Get amount of XP towards next level
        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }

    // Give or take EXP
    public static int changePlayerExp(Player player, int exp){
        // Get player's current exp
        int currentExp = getPlayerExp(player);

        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);

        // Give the player their exp back, with the difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);

        // Return the player's new exp amount
        return newExp;
    }
}
