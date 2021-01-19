package EDGRRRR.DCE.Main;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.json.simple.JSONObject;

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
    private double minSendAmount = 0.01;
    private String itemsFile = "items.json";
    private boolean minAmountLimit = true;
    private double minAmount = 0;


    public EconomyM(App app) {
        this.app = app;
        // Items
        this.aliases = null;
        this.materials = null;        
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
        return economy.getBalance(player);
    }

    /**
     * Rounding
     * @param amount
     */

     public double round(double amount) {
        amount *= 100;
        int intAmount = (int) amount;
        amount = (double) intAmount;
        return amount / 100;
     }

    /**
     * Adds <amount> from <player>
     * @param player
     * @param amount
     */
    public EconomyResponse addCash(Player player, double amount) {
        amount = round(amount);
        EconomyResponse response = economy.depositPlayer(player, amount);
        response = new EconomyResponse(response.amount, getBalance(player), response.type, response.errorMessage);
        return response;
    }

    /**
     * Removes <amount> from <player>
     * @param player
     * @param amount
     */
    public EconomyResponse remCash(Player player, double amount) {
        amount = round(amount);
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        response = new EconomyResponse(response.amount, getBalance(player), response.type, response.errorMessage);
        return response;
    }

    public EconomyResponse setCash(Player player, double amount) {
        amount = round(amount);
        double balance = getBalance(player);
        double difference = amount - balance;
        EconomyResponse response = null;
        if (difference < 0) {
            response = this.remCash(player, -difference);
        } else if (difference > 0) {
            response = this.addCash(player, difference);
        } else if (difference == 0) {
            response = new EconomyResponse(difference, getBalance(player), ResponseType.SUCCESS, "");
        }

        response = new EconomyResponse(response.amount, getBalance(player), response.type, response.errorMessage);
        return response; 
    }

    // JSON STUFF
    private JSONObject readConfig() {
        return null;
    }
}