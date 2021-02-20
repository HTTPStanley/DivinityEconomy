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
    private final DCEPlugin app;

    // Stores the Vault economy api
    private Economy economy;

    // Settings
    public double minSendAmount;
    public int roundingDigits;
    public int baseQuantity;
    public double tax;
    public double minAccountBalance;

    public EconomyManager(DCEPlugin app) {
        this.app = app;

        // settings
        this.minSendAmount = this.app.getConfig().getDouble(this.app.getConf().strEconMinSendAmount);
        this.tax = this.app.getConfig().getDouble(this.app.getConf().strEconTaxScale);
        this.roundingDigits = this.app.getConfig().getInt(this.app.getConf().strEconRoundingDigits);
        this.baseQuantity = this.app.getConfig().getInt(this.app.getConf().strEconBaseQuantity);
        this.minAccountBalance = this.app.getConfig().getDouble(this.app.getConf().strEconMinAccountBalance);
    }


    /**
     * Sets up the vault economy object
     * Returns if it was successful or not.
     */
    public void setupEconomy() {
        // Look for vault
        if (this.app.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.app.getCon().severe("No plugin 'Vault' detected.");
            return;
        } else {
            this.app.getCon().info("Vault has been detected.");
        }

        // Get the service provider
        RegisteredServiceProvider<Economy> rsp = this.app.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            this.app.getCon().severe("Could not register Economy.");
            return;
        } else {
            this.app.getCon().info("Registered Economy.");
        }

        // return if economy was gotten successfully.
        this.economy = rsp.getProvider();
    }

    /**
     * Returns the vault economy api
     * @return Economy
     */
    public Economy getEconomy() {
        return this.economy;
    }

    /**
     * Gets the players balance
     * @param player - The player
     * @return double
     */
    public double getBalance(Player player) {
        return this.getBalance((OfflinePlayer) player);
    }

    /**
     * Gets the players balance
     * @return double
     */
    public double getBalance(OfflinePlayer oPlayer) {
        return this.economy.getBalance(oPlayer);
    }

    /**
     * Rounding
     * @param amount - The amount
     */

     public double round(double amount) {
        return this.round(amount, this.roundingDigits);
    }

    public double round(double amount, int roundingDigits) {
        // Rounds the amount to the number of digits specified
        // Does this by 10**digits (100 or 10**2 = 2 digits)
        try {
            double roundAmount = Math.pow(10, roundingDigits);
            return Math.round(amount * roundAmount) / roundAmount;
        } catch (Exception e) {
            this.app.getCon().severe("Rounding error for value (" + amount + " (" + roundingDigits + ")): " + e.getMessage());
            return amount;
        }

    }

    /**
     * Adds <amount> to <player>
     * @param oPlayer - The offline player
     * @param amount - The amount
     */
    public EconomyResponse addCash(OfflinePlayer oPlayer, double amount) {
        this.app.getCon().debug("ADD REQUEST '" + oPlayer.getName() + "' £" + amount);
        EconomyResponse response = this.economy.depositPlayer(oPlayer, amount);
        response = new EconomyResponse(response.amount, this.getBalance(oPlayer), response.type, response.errorMessage);
        this.app.getCon().debug("ADD COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    /**
     * Adds <amount> to <player>
     * @param player - The player
     * @param amount - The amount
     */
    public EconomyResponse addCash(Player player, double amount) {
        return this.addCash((OfflinePlayer) player, amount);
    }

    /**
     * Removes <amount> from <player>
     * @param oPlayer - The offline player
     * @param amount - The amount
     */
    public EconomyResponse remCash(OfflinePlayer oPlayer, double amount) {
        this.app.getCon().debug("SET REQUEST '" + oPlayer.getName() + "' £" + amount);
        EconomyResponse response = this.economy.withdrawPlayer(oPlayer, amount);
        response = new EconomyResponse(response.amount, this.getBalance(oPlayer), response.type, response.errorMessage);
        this.app.getCon().debug("REM COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    /**
     * Removes <amount> from <player>
     * @param player - The player
     * @param amount - The amount
     */
    public EconomyResponse remCash(Player player, double amount) {
        return this.remCash((OfflinePlayer) player, amount);
    }

    /**
     * Sets the balance of a player to the amount provided
     * @param oPlayer - The offline player
     * @param amount - The amount
     * @return EconomyResponse - The result of the function
     */
    public EconomyResponse setCash(OfflinePlayer oPlayer, double amount) {
        this.app.getCon().debug("SET REQUEST '" + oPlayer.getName() + "' £" + amount);
        double balance = this.getBalance(oPlayer);
        double difference = amount - balance;
        EconomyResponse response = null;
        if (difference < 0) {
            response = this.remCash(oPlayer, -difference);
        } else if (difference > 0) {
            response = this.addCash(oPlayer, difference);
        } else if (difference == 0) {
            response = new EconomyResponse(difference, this.getBalance(oPlayer), ResponseType.SUCCESS, "");
        }

        response = new EconomyResponse(response.amount, this.getBalance(oPlayer), response.type, response.errorMessage);
        this.app.getCon().debug("SET COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    /**
     * Sets the balance of a player to the amount provided
     * @param player - The player
     * @param amount - The amount
     * @return EconomyResponse - The result of the command
     */
    public EconomyResponse setCash(Player player, double amount) {
        return this.setCash((OfflinePlayer) player, amount);
    }

    /**
     * Removes amount <amount> from <from>
     * Adds amount <amount> to <to>
     * Can fail if criteria aren't met:
     * -minimumSendAmount <
     * -<from> has <amount> to send
     * @param from - The source player of cash
     * @param to - The result player of cash
     * @param amount - The amount of cash
     * @return EconomyResponse - The result of the function
     */
    public EconomyResponse sendCash(Player from, OfflinePlayer to, double amount) {
        double fromBalance = this.getBalance(from);

        EconomyResponse takeResponse = this.remCash(from, amount);
        if (takeResponse.type == ResponseType.FAILURE) {
            return takeResponse;
        }

        EconomyResponse sendResponse = this.addCash(to, amount);
        if (sendResponse.type == ResponseType.FAILURE) {
            // Since takeResponse was a success
            // We have to reset their balance
            this.setCash(from, fromBalance);
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
     * @param from - The source player of cash
     * @param to - The result player of cash
     * @param amount - The amount of cash
     * @return EconomyResponse - The result of the function
     */
    public EconomyResponse sendCash(Player from, Player to, double amount) {
        return this.sendCash(from, (OfflinePlayer) to, amount);
    }

    /**
     * A function for extracting a double from a String
     * will return null if an error occurs (such as the string not containing a double)
     * @param arg - A string to convert to a double
     * @return double - A potentially converted double
     */
    public double getDouble(String arg) {
        // Instantiate amount
        double amount;

        // Try to parse the double
        // Catch the error and set to null
        try {
            amount = Double.parseDouble(arg);
        } catch (Exception e) {
            amount = 0.0;
        }

        return amount;
    }
}
