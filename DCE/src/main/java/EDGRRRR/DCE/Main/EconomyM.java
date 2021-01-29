package EDGRRRR.DCE.Main;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * An economy manager to simplify tasks for managing the player economy, works with Vault Economy.
 */
public class EconomyM {

    // Stores the main app
    private App app;

    // Stores the Vault economy api
    private Economy economy;

    // Stores items
    private HashMap<String, String> aliases;
    private HashMap<String, Object> materials;

    // Settings
    private double minSendAmount;
    private double roundingDigits;
    private String itemsFile = "items.json";


    public EconomyM(App app) {
        this.app = app;
        // Items
        this.aliases = null;
        this.materials = null;

        // settings
        this.minSendAmount = app.getConfig().getDouble(app.getConf().strEconMinSendAmount);
        this.roundingDigits = app.getConfig().getDouble(app.getConf().strEconRoundingDigits);
        app.getLogger().info("Min send amount: " + minSendAmount);
        app.getLogger().info("Rounding digits: " + roundingDigits);
    }


    /**
     * Sets up the vault economy object
     * Returns if it was successful or not.
     * @return boolean
     */
    public boolean setupEconomy() {
        // Look for vault
        if (app.getServer().getPluginManager().getPlugin("Vault") == null) {
            app.getCon().severe("No plugin 'Vault' detected.");
            return false;
        } else {
            app.getCon().info("Vault has been detected.");
        }

        // Get the service provider
        RegisteredServiceProvider<Economy> rsp = app.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            app.getCon().severe("Could not register Economy.");
            return false;
        } else {
            app.getCon().info("Registered Economy.");
        }

        // return if economy was gotten successfully.
        economy = rsp.getProvider();
        return economy != null;
    }

    /**
     * Returns the vault economy api
     * @return Economy
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Gets the players balance
     * @param player
     * @return double
     */
    public double getBalance(Player player) {
        OfflinePlayer oPlayer = (OfflinePlayer) player;
        return getBalance(oPlayer);
    }
    public double getBalance(OfflinePlayer oPlayer) {
        return economy.getBalance(oPlayer);
    }

    /**
     * Rounding
     * @param amount
     */

     public double round(double amount) {
        // Rounds the amount to the number of digits specified
        // Does this by 10**digits (100 or 10**2 = 2 digits)
        double roundAmount = Math.pow(10, roundingDigits);
        return Math.round(amount * roundAmount) / roundAmount;

    }

    // /**
    //  * Lgeacy Rounding
    //  * @param amount
    //  */

    //  public double legacyRound(double amount) {
    //     amount *= 100;
    //     int intAmount = (int) amount;
    //     amount = (double) intAmount;
    //     return amount / 100;
    //  }

    /**
     * Adds <amount> from <player>
     * @param player
     * @param amount
     */
    public EconomyResponse addCash(OfflinePlayer oPlayer, double amount) {
        app.getCon().debug("ADD REQUEST '" + oPlayer.getName() + "' £" + amount);
        amount = round(amount);
        EconomyResponse response = economy.depositPlayer(oPlayer, amount);
        response = new EconomyResponse(response.amount, getBalance(oPlayer), response.type, response.errorMessage);
        app.getCon().debug("ADD COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    public EconomyResponse addCash(Player player, double amount) {
        OfflinePlayer oPlayer = (OfflinePlayer) player;
        return addCash(oPlayer, amount);
    }

    /**
     * Removes <amount> from <player>
     * @param player
     * @param amount
     */
    public EconomyResponse remCash(OfflinePlayer oPlayer, double amount) {
        app.getCon().debug("SET REQUEST '" + oPlayer.getName() + "' £" + amount);
        amount = round(amount);
        EconomyResponse response = economy.withdrawPlayer(oPlayer, amount);
        response = new EconomyResponse(response.amount, getBalance(oPlayer), response.type, response.errorMessage);
        app.getCon().debug("REM COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    public EconomyResponse remCash(Player player, double amount) {
        OfflinePlayer oPlayer = (OfflinePlayer) player;
        return remCash(oPlayer, amount);
    }

    public EconomyResponse setCash(OfflinePlayer oPlayer, double amount) {
        app.getCon().debug("SET REQUEST '" + oPlayer.getName() + "' £" + amount);
        amount = round(amount);
        double balance = getBalance(oPlayer);
        double difference = amount - balance;
        EconomyResponse response = null;
        if (difference < 0) {
            response = remCash(oPlayer, -difference);
        } else if (difference > 0) {
            response = addCash(oPlayer, difference);
        } else if (difference == 0) {
            response = new EconomyResponse(difference, getBalance(oPlayer), ResponseType.SUCCESS, "");
        }

        response = new EconomyResponse(response.amount, getBalance(oPlayer), response.type, response.errorMessage);
        app.getCon().debug("SET COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    public EconomyResponse setCash(Player player, double amount) {
        OfflinePlayer oPlayer = (OfflinePlayer) player;
        return setCash(oPlayer, amount);
    }

    public EconomyResponse sendCash(Player from, OfflinePlayer to, double amount) {
        double fromBalance = getBalance(from);

        EconomyResponse takeResponse = remCash(from, amount);
        if (takeResponse.type == ResponseType.FAILURE) {
            return takeResponse;
        }

        EconomyResponse sendResponse = addCash(to, amount);
        if (sendResponse.type == ResponseType.FAILURE) {
            // Since takeResponse was a success
            setCash(from, fromBalance);
            return sendResponse;
        }

        return new EconomyResponse(amount, fromBalance, ResponseType.SUCCESS, null);
    }
    public EconomyResponse sendCash(Player from, Player to, double amount) {
        OfflinePlayer oPlayer = (OfflinePlayer) to;
        return sendCash(from, oPlayer, amount);
    }

    public Double getDouble(String arg) {
        Double amount = null;
        try {
            amount = Double.parseDouble(arg);
        } catch (Exception e) {
            amount = null;
        }

        return amount;
    }
}