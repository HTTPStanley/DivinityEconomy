package EDGRRRR.DCE.Main;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

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
    private static Economy econ = null;

    // The console
    private static Console console = null;

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
            log.severe("Console setup has failed.");
            exit();
            return;
        }
        if (!setupEconomy()) {
            console.severe("Economy hook failed.");
            exit();
            return;
        }

        console.info("Plugin Enabled");
    }

    /** 
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        console.info("Plugin Disabled");
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        
        console.info(player.getName() + " said " + command.getName() + " " + interpret(args));

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
        console = new Console();
        return console != null;     
    }

    /** 
     * Setup the economy and store in this.econ
     * returns if it was successfull or not
     * @return boolean
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            console.severe("No plugin 'Vault' detected.");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            console.severe("Could not register Economy.");
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    /**
     * Returns the economy
     * @return Economy
     */
    public static Economy getEconomy() {
        return econ;
    }

    /**
     * Disables the plugin
     */
    private void exit() {
        console.warn("Shutting down.");
        getServer().getPluginManager().disablePlugin(this);
    }
}