package EDGRRRR.DCE.Main;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

/**
 * An economy manager to simplify tasks for managing the player economy, works with Vault Economy.
 */
public class EconomyM {
    // Stores the Vault economy object
    private static Economy economy = null;


    /**
     * Sets up the vault economy object
     * Returns if it was successful or not.
     * @return boolean
     */
    public boolean setupEconomy() {
        // Get the app
        App.get();
        
        // Look for vault
        if (App.get().getServer().getPluginManager().getPlugin("Vault") == null) {            
            App.getCon().severe("No plugin 'Vault' detected.");
            return false;
        } else {
            App.getCon().info("Vault has been detected.");
        }

        // Get the service provider
        RegisteredServiceProvider<Economy> rsp = App.get().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            App.getCon().severe("Could not register Economy.");
            return false;
        } else {
            App.getCon().info("Registered Economy.");
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
        return getEconomy().getBalance(player);
    }

}