package EDGRRRR.DCE.Main;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * The Main Class of the plugin
 * Hooks everything together
 */
public class App extends JavaPlugin {
    // The logger
    private static final Logger log = Logger.getLogger("Minecraft");
    
    // Accessor method
    private static App i;
    public static App get() { return i; }

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
        // Maintains accessor method.
        i = this;

        if (!setupConsole()) {
            log.severe("Console setup failed.");
            exit();
            return;
        }
        if (!setupEconomy()) {
            con.severe("Economy setup failed.");
            exit();
            return;
        }

        con.info("Plugin Enabled");
    }

    /** 
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        con.info("Plugin Disabled");
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        
        con.info(player.getName() + " said " + command.getName() + " " + interpret(args));

        return true;
    }

    public String interpret(String[] args) {
        String argString = "";
        for (String arg:args){
            argString += arg + ", ";
        }
        return argString;
    }

    /**
     * Setup the console and store in this.console
     * returns if it was successfull or not
     * @return boolean
     */
    private boolean setupConsole() {
        con = new Console();
        return con != null;     
    }

    /** 
     * Setup the economy and store in this.eco
     * returns if it was successfull or not
     * @return boolean
     */
    private boolean setupEconomy() {
        // Create the economy object
        eco = new EconomyM();

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
    public static EconomyM getEco() {
        return eco;
    }
    public static Console getCon() {
        return con;
    }

    /**
     * Disables the plugin
     */
    private void exit() {
        con.warn("Shutting down.");
        getServer().getPluginManager().disablePlugin(this);
    }
}