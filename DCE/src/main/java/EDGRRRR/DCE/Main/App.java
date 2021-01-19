package EDGRRRR.DCE.Main;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import EDGRRRR.DCE.Main.commands.Balance;
import EDGRRRR.DCE.Main.commands.EditCash;
import EDGRRRR.DCE.Main.commands.Ping;
import EDGRRRR.DCE.Main.commands.SendCash;
/**
 * The Main Class of the plugin
 * Hooks everything together
 */
public class App extends JavaPlugin {
    // The logger
    private static final Logger log = Logger.getLogger("DCE");

    // The economy
    protected static EconomyM eco = null;

    // The console
    protected static Console con = null;


    /**
     * Called when the plugin is enabled
     * Setup console
     * Setup Economy
     */
    @Override
    public void onEnable() {
        // Setup the console class
        if (!setupConsole()) {
            log.severe("Console setup failed.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Setup the economy class
        if (!setupEconomy()) {
            con.severe("Economy setup failed.");
            exit();
            return;
        }

        // Command registry
        try {
            // Register Ping class
            getCommand("ping").setExecutor(new Ping(this));
            // Register Balance class
            getCommand("balance").setExecutor(new Balance(this));
            // Register AddCash class
            getCommand("editcash").setExecutor(new EditCash(this));
            // Register SendCash class
            getCommand("sendcash").setExecutor(new SendCash(this));
        } catch(Exception e) {
            con.warn("An error has occurred on command registry.");
            con.severe("Error: " + e);
        }

        // Done :)
        con.info("Plugin Enabled");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        con.info("Plugin Disabled");
    }

    /**
     * Setup the console and store in this.console
     * returns if it was successfull or not
     * @return boolean
     */
    private boolean setupConsole() {
        // Create console
        con = new Console(this);
        return con != null;
    }

    /**
     * Setup the economy and store in this.eco
     * returns if it was successfull or not
     * @return boolean
     */
    private boolean setupEconomy() {
        // Create the economy object
        eco = new EconomyM(this);

        // If eco is null, return false startup
        if (eco == null) {
            return false;
        }

        // SetupEconomy - return false if fails.
        if (eco.setupEconomy() == false) {
            return false;
        }

        // Return successful setup
        return true;
    }


    /**
     * Returns the economy
     * @return Economy
     */
    public EconomyM getEco() {
        return eco;
    }

    /**
     * Returns the console
     * @return Console
     */
    public Console getCon() {
        return con;
    }

    /**
     * Disables the plugin
     */
    private void exit() {
        // Shutdown message
        con.warn("Shutting down.");
        // Disable plugin
        getServer().getPluginManager().disablePlugin(this);
    }
}