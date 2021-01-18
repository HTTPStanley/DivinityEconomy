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
    private final Logger log = Logger.getLogger("DCE");

    // The economy
    protected EconomyM eco = null;
    // The console
    protected Console con = null;


    /**
     * Called when the plugin is enabled
     * Setup console
     * Setup Economy
     */
    @Override
    public void onEnable() {
    	//Setup Managers
    	this.con = new Console(this);
    	this.eco = new EconomyM(this);
    	
    	
        // Register Ping class
        this.getCommand("ping").setExecutor(new Ping(this));
        // Register Balance class
        this.getCommand("balance").setExecutor(new Balance(this));
        // Register AddCash class
        this.getCommand("editcash").setExecutor(new EditCash(this));
        // Register SendCash class
        this.getCommand("sendcash").setExecutor(new SendCash(this));


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

}