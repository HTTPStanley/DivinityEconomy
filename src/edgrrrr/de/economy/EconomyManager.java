package edgrrrr.de.economy;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.response.EconomyTransferResponse;
import edgrrrr.vea.economy.EconomyAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.Collection;

/**
 * An economy manager to simplify tasks for managing the player economy, works with Vault Economy.
 */
public class EconomyManager {

    // Settings
    public final double minTransfer;
    public final int roundingDigits;
    public final double minBalance;
    private final String providerName;
    // Stores the main app
    private final DEPlugin app;
    // Stores the Vault economy api
    private Economy economy;

    public EconomyManager(DEPlugin app) {
        this.app = app;

        // settings
        this.minTransfer = this.app.getConfigManager().getDouble(Setting.ECONOMY_MIN_SEND_AMOUNT_DOUBLE);
        this.roundingDigits = this.app.getConfigManager().getInt(Setting.ECONOMY_ACCURACY_DIGITS_INTEGER);
        this.minBalance = this.app.getConfigManager().getDouble(Setting.ECONOMY_MIN_BALANCE_DOUBLE);
        this.providerName = this.app.getConfigManager().getString(Setting.ECONOMY_PROVIDER_STRING);
    }

    public Collection<RegisteredServiceProvider<Economy>> getProviders() {
        return this.app.getServer().getServicesManager().getRegistrations(Economy.class);
    }

    public RegisteredServiceProvider<Economy> getPrimaryProvider() {
        for (RegisteredServiceProvider<Economy> provider : this.getProviders()) {
            if (provider.getPlugin().getName().equals(this.providerName)) {
                return provider;
            }
        }
        return this.app.getServer().getServicesManager().getRegistration(Economy.class);
    }

    public Collection<RegisteredServiceProvider<Economy>> setProvider(Economy economy) {
        this.app.getServer().getServicesManager().register(Economy.class, economy, this.app, ServicePriority.Normal);
        return this.getProviders();
    }


    /**
     * Sets up the vault economy object
     * Returns if it was successful or not.
     */
    public boolean setupEconomy() {
        EconomyAPI economy = new EconomyAPI(this.app, this.app.getConfigManager(), this.app.getConsole(), this.app.getPlayerManager(), this.app.getConfigManager().getInt(Setting.ECONOMY_ACCURACY_DIGITS_INTEGER), "coins", "coin");

        // Look for vault
        if (this.app.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.app.getConsole().warn("No plugin 'Vault' detected, this will likely cause issues with plugins not cooperating.");
            this.economy = economy;

        } else {
            this.app.getConsole().info("Vault has been detected.");


            // Get the service provider
            Collection<RegisteredServiceProvider<Economy>> providers = this.setProvider(economy);
            if (providers.size() == 0) {
                this.app.getConsole().severe("Could not register Economy.");
                return false;
            } else {
                this.economy = this.getPrimaryProvider().getProvider();
            }

            for (RegisteredServiceProvider<Economy> provider : providers) {
                this.app.getConsole().info(String.format("Registered Economy Provider: '%s' (primary = %s) (selected = %s)", provider.getPlugin().getName(), provider.equals(this.getPrimaryProvider()), provider.getPlugin().getName().equals(this.providerName)));
            }
            this.app.getConsole().info(String.format("Total Economy Providers: %d", providers.size()));
        }

        // return if economy was gotten successfully.
        return true;
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
     * Adds <amount> to <player>
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     */
    public EconomyResponse addCash(OfflinePlayer oPlayer, double amount) {
        this.app.getConsole().debug(String.format("ADD REQUEST FOR %s £%,.2f", oPlayer.getName(), amount));
        EconomyResponse response = this.economy.depositPlayer(oPlayer, amount);
        this.app.getConsole().debug(String.format("ADD RESULT: %s %s", response.transactionSuccess(), response.errorMessage));
        return response;
    }

    /**
     * Removes <amount> from <player>
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     */
    public EconomyResponse remCash(OfflinePlayer oPlayer, double amount) {
        this.app.getConsole().debug(String.format("REM REQUEST FOR %s £%,.2f", oPlayer.getName(), amount));
        EconomyResponse response = this.economy.withdrawPlayer(oPlayer, amount);
        this.app.getConsole().debug(String.format("REM RESULT: %s %s", response.transactionSuccess(), response.errorMessage));

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
        this.app.getConsole().debug(String.format("SET REQUEST FOR %s £%,.2f", oPlayer.getName(), amount));
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

        this.app.getConsole().debug(String.format("SET RESULT: %s %s", response.transactionSuccess(), response.errorMessage));

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
            if (amount < this.minTransfer) {
                response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, ResponseType.FAILURE, String.format("cannot send less than £%f", this.minTransfer));
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
