package edgrrrr.dce.economy;

import edgrrrr.dce.main.DCEPlugin;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.response.EconomyTransferResponse;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * An economy manager to simplify tasks for managing the player economy, works with Vault Economy.
 */
public class EconomyManager {

    // Settings
    public final double minSendAmount;
    public final int roundingDigits;
    public final double minAccountBalance;
    // Stores the main app
    private final DCEPlugin app;
    // Stores the Vault economy api
    private Economy economy;

    public EconomyManager(DCEPlugin app) {
        this.app = app;

        // settings
        this.minSendAmount = this.app.getConfig().getDouble(this.app.getConfigManager().strEconomyMinSendAmount);
        this.roundingDigits = this.app.getConfig().getInt(this.app.getConfigManager().strEconomyRoundingDigits);
        this.minAccountBalance = this.app.getConfig().getDouble(this.app.getConfigManager().strEconomyMinAccountBalance);
    }


    /**
     * Sets up the vault economy object
     * Returns if it was successful or not.
     */
    public void setupEconomy() {
        // Look for vault
        if (this.app.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.app.getConsoleManager().severe("No plugin 'Vault' detected.");
            return;
        } else {
            this.app.getConsoleManager().info("Vault has been detected.");
        }

        // Get the service provider
        RegisteredServiceProvider<Economy> rsp = this.app.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            this.app.getConsoleManager().severe("Could not register Economy.");
            return;
        } else {
            this.app.getConsoleManager().info("Registered Economy.");
        }

        // return if economy was gotten successfully.
        this.economy = rsp.getProvider();
    }

    /**
     * Returns the vault economy api
     *
     * @return Economy
     */
    public Economy getEconomy() {
        return this.economy;
    }

    /**
     * Gets the players balance
     * @param player - The player to get the balance for
     * @return double
     */
    public double getBalance(OfflinePlayer player) {
        return this.economy.getBalance(player);
    }

    /**
     * Returns the players balance, rounded.
     * @param player - The player to get the balance for
     * @return double - The rounded balance
     */
    public double getRoundedBalance(OfflinePlayer player) {
        return this.round(this.getBalance(player));
    }

    /**
     * Rounding
     *
     * @param amount - The amount
     */
    public double round(double amount) {
        return Math.round(amount, this.roundingDigits);
    }


    /**
     * Adds <amount> to <player>
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     */
    public EconomyResponse addCash(OfflinePlayer oPlayer, double amount) {
        this.app.getConsoleManager().debug("ADD REQUEST '" + oPlayer.getName() + "' £" + amount);
        EconomyResponse response = this.economy.depositPlayer(oPlayer, amount);
        response = new EconomyResponse(response.amount, this.getBalance(oPlayer), response.type, response.errorMessage);
        this.app.getConsoleManager().debug("ADD COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    /**
     * Removes <amount> from <player>
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     */
    public EconomyResponse remCash(OfflinePlayer oPlayer, double amount) {
        this.app.getConsoleManager().debug("REM REQUEST '" + oPlayer.getName() + "' £" + amount);
        double oldBalance = this.getBalance(oPlayer);
        EconomyResponse response;
        if ((oldBalance - amount) < this.minAccountBalance) {
            response = new EconomyResponse(amount, oldBalance, ResponseType.FAILURE, String.format("not enough cash for this transfer (£%,.2f/£%,.2f)", oldBalance, amount));
        } else {
            response = this.economy.withdrawPlayer(oPlayer, amount);
        }
        response = new EconomyResponse(response.amount, this.getBalance(oPlayer), response.type, response.errorMessage);
        this.app.getConsoleManager().debug("REM COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    /**
     * Sets the balance of a player to the amount provided
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     * @return EconomyResponse - The result of the function
     */
    public EconomyResponse setCash(OfflinePlayer oPlayer, double amount) {
        this.app.getConsoleManager().debug("SET REQUEST '" + oPlayer.getName() + "' £" + amount);
        double balance = this.getBalance(oPlayer);
        double difference = amount - balance;
        EconomyResponse response;
        if (difference < 0) {
            response = this.remCash(oPlayer, -difference);
        } else if (difference > 0) {
            response = this.addCash(oPlayer, difference);
        } else {
            response = new EconomyResponse(difference, this.getBalance(oPlayer), ResponseType.SUCCESS, "");
        }

        response = new EconomyResponse(response.amount, this.getBalance(oPlayer), response.type, response.errorMessage);
        this.app.getConsoleManager().debug("SET COMPLETE '" + oPlayer.getName() + "' £" + response.balance + "(£ " + response.amount + ")");
        return response;
    }

    /**
     * Removes amount <amount> from <from>
     * Adds amount <amount> to <to>
     * Can fail if criteria aren't met:
     * -minimumSendAmount <
     * -<from> has <amount> to send
     *
     * @param from   - The source player of cash
     * @param to     - The result player of cash
     * @param amount - The amount of cash
     * @return EconomyResponse - The result of the function
     */
    public EconomyTransferResponse sendCash(OfflinePlayer from, OfflinePlayer to, double amount) {
        // The response
        EconomyTransferResponse response;
        double fromBalance = this.getBalance(from);
        double toBalance = this.getBalance(to);

        if (from == to) {
            response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, ResponseType.FAILURE, "cannot send money to yourself!");
        } else {
            // Ensure amount is above or equal to the minimum send amount
            if (amount < this.minSendAmount) {
                response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, ResponseType.FAILURE, String.format("cannot send less than £%f", this.minSendAmount));
            } else {

                // Take money from sender
                EconomyResponse takeResponse = this.remCash(from, amount);
                if (takeResponse.type == ResponseType.FAILURE) {
                    response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, ResponseType.FAILURE, takeResponse.errorMessage);

                } else {

                    // Send money to receiver
                    EconomyResponse sendResponse = this.addCash(to, amount);
                    if (sendResponse.type == ResponseType.FAILURE) {
                        // Since takeResponse was a success
                        // We have to reset their balance
                        this.setCash(from, fromBalance);
                        response = new EconomyTransferResponse(this.getBalance(from), toBalance, 0.0, ResponseType.FAILURE, sendResponse.errorMessage);
                    } else {
                        response = new EconomyTransferResponse(this.getBalance(from), this.getBalance(from), amount, ResponseType.SUCCESS, "");
                    }
                }
            }
        }
        return response;
    }
}
