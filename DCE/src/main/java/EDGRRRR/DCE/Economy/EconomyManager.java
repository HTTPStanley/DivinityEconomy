package EDGRRRR.DCE.Economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import EDGRRRR.DCE.Main.DCEPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * An economy manager to simplify tasks for managing the player economy, works with Vault Economy.
 */
public class EconomyManager {

    // Stores the main app
    private DCEPlugin app;

    // Stores the Vault economy api
    private Economy economy;

    // Settings
    public double minSendAmount;
    public int roundingDigits;
    public int baseQuantity;
    public double tax;

    public EconomyManager(DCEPlugin app) {
        this.app = app;

        // settings
        this.minSendAmount = app.getConfig().getDouble(app.getConf().strEconMinSendAmount);
        this.tax = app.getConfig().getDouble(app.getConf().strEconTaxScale);
        this.roundingDigits = app.getConfig().getInt(app.getConf().strEconRoundingDigits);
        this.baseQuantity = app.getConfig().getInt(app.getConf().strEconBaseQuantity);
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

    /**
     * Gets the players balance
     * @param player
     * @return double
     */
    public double getBalance(OfflinePlayer oPlayer) {
        return economy.getBalance(oPlayer);
    }

    /**
     * Rounding
     * @param amount
     */

     public double round(double amount) {
        return round(amount, this.roundingDigits);
    }

    public double round(double amount, int roundingDigits) {
        // Rounds the amount to the number of digits specified
        // Does this by 10**digits (100 or 10**2 = 2 digits)
        double roundAmount = Math.pow(10, roundingDigits);
        return Math.round(amount * roundAmount) / roundAmount;
    }

    /**
     * Adds <amount> to <player>
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

    /**
     * Adds <amount> to <player>
     * @param player
     * @param amount
     */
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

    /**
     * Removes <amount> from <player>
     * @param player
     * @param amount
     */
    public EconomyResponse remCash(Player player, double amount) {
        OfflinePlayer oPlayer = (OfflinePlayer) player;
        return remCash(oPlayer, amount);
    }

    /**
     * Sets the balance of a player to the amount provided
     * @param oPlayer
     * @param amount
     * @return
     */
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

    /**
     * Sets the balance of a player to the amount provided
     * @param oPlayer
     * @param amount
     * @return
     */
    public EconomyResponse setCash(Player player, double amount) {
        OfflinePlayer oPlayer = (OfflinePlayer) player;
        return setCash(oPlayer, amount);
    }

    /**
     * Removes amount <amount> from <from>
     * Adds amount <amount> to <to>
     * Can fail if criteria aren't met:
     * -minimumSendAmount <
     * -<from> has <amount> to send
     * @param from
     * @param to
     * @param amount
     * @return
     */
    public EconomyResponse sendCash(Player from, OfflinePlayer to, double amount) {
        double fromBalance = getBalance(from);

        EconomyResponse takeResponse = remCash(from, amount);
        if (takeResponse.type == ResponseType.FAILURE) {
            return takeResponse;
        }

        EconomyResponse sendResponse = addCash(to, amount);
        if (sendResponse.type == ResponseType.FAILURE) {
            // Since takeResponse was a success
            // We have to reset their balance
            setCash(from, fromBalance);
            return sendResponse;
        }

        return new EconomyResponse(amount, fromBalance, ResponseType.SUCCESS, null);
    }

    /**
     * Removes amount <amount> from <from>
     * Adds amount <amount> to <to>
     * Can fail if criteria aren't met:
     * -minimumSendAmount <
     * -<from> has <amount> to send
     * @param from
     * @param to
     * @param amount
     * @return
     */
    public EconomyResponse sendCash(Player from, Player to, double amount) {
        OfflinePlayer oPlayer = (OfflinePlayer) to;
        return sendCash(from, oPlayer, amount);
    }

    /**
     * A function for extracting a double from a String
     * will return null if an error occurrs (such as the string not containing a double)
     * @param arg
     * @return
     */
    public Double getDouble(String arg) {
        // Instantiate amount
        Double amount = null;

        // Try to parse the double
        // Catch the error and set to null
        try {
            amount = Double.parseDouble(arg);
        } catch (Exception e) {
            amount = null;
        }

        return amount;
    }
}