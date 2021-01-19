package EDGRRRR.DCE.Main;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.json.simple.JSONObject;

import net.milkbowl.vault.economy.Economy;

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
    private Double minSendAmount = 0.01;
    private String itemsFile = "items.json";


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
        if (app.get().getServer().getPluginManager().getPlugin("Vault") == null) {
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
     * @return Double
     */
    public Double getBalance(Player player) {
        return economy.getBalance(player);
    }

    /**
     * Adds <amount> from <player>
     * @param player
     * @param amount
     */
    public void addCash(Player player, Double amount) {
        economy.depositPlayer(player, amount);
    }

    /**
     * Removes <amount> from <player>
     * @param player
     * @param amount
     */
    public void remCash(Player player, Double amount) {
        economy.withdrawPlayer(player, amount);
    }

    /**
     * Moves cash <amount> from <from> to <to>
     * Has a series of checks to ensure this is possible, also sends messages regarding the transfer
     * @param from
     * @param to
     * @param amount
     */
    public void sendCash(Player from, Player to, Double amount) {
        // Stores if checks completed successfully
        boolean success = true;
        // Stores reason for failure
        String reason = "";

        // Stores repeated information - eases readibility and read calls.
        Double fromBal = getBalance(from);
        String fromName = from.getName();
        String toName = to.getName();

        // Check <from> user has enough cash
        if (fromBal < amount) {
            // Set the reason and failure
            reason = "you only have £" + fromBal;
			success = false;
        }
        // Check <amount> is greater than minSendAmount
        else if (amount < minSendAmount) {
            // Set the reason and failure
            reason = "the minimum send amount is £" + minSendAmount;
            success = false;
        }

        if (success == true) {
            try {
                // Withdraw <amount> from <from>
                economy.withdrawPlayer(from, amount);
                // Deposity <amount> to <to>
                economy.depositPlayer(to, amount);
                // Send message to <from>
                Double newFromBalance = getBalance(from);
                Double newToBalance = getBalance(to);
                app.getCon().info(from, "Successfully sent £" + amount + " to " + toName + ". Your new balance is £" + newFromBalance);
                // Send message to <to>
                app.getCon().info(to, "Received £" + amount + " from " + fromName + ". Your new balance is £" + newToBalance);
                // Make Log
                app.getCon().info(fromName + " sent £" + amount + " to " + toName + ". Their new balances are £" + newFromBalance + " | £" + newToBalance + " respectively.");
            } catch(Exception e) {
                // If an error ocurred. Log it.
                app.getCon().severe("A transaction error occurred when "+ fromName + " tried to send £" + amount + " to " + toName);
                app.getCon().severe("Error: " + e);
                // Perhaps try reset bank values, but if an error occurred in this situ it's likely to reoccur.
            }
        } else {
            app.getCon().warn(from, "Cannot send £" + amount + " to " + toName + " as " + reason);
        }
        // Return
        return;

    }

    // JSON STUFF
    private JSONObject readConfig() {
        return null;
    }
}