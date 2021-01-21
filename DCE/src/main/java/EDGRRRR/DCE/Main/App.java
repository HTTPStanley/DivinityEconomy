package EDGRRRR.DCE.Main;

import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import EDGRRRR.DCE.Main.commands.Balance;
import EDGRRRR.DCE.Main.commands.ClearBal;
import EDGRRRR.DCE.Main.commands.EditBal;
import EDGRRRR.DCE.Main.commands.Ping;
import EDGRRRR.DCE.Main.commands.SendCash;
import EDGRRRR.DCE.Main.commands.SetBal;
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
        this.eco.setupEconomy();
    	
    	try {
            // Register Ping class
            getCommand("ping").setExecutor(new Ping(this));
            // Register Balance class
            getCommand("balance").setExecutor(new Balance(this));
            // Register AddCash class
            getCommand("editbal").setExecutor(new EditBal(this));
            // Register SendCash class
            getCommand("sendcash").setExecutor(new SendCash(this));
            // Register SetBal class
            getCommand("setbal").setExecutor(new SetBal(this));
            // Register ClearBal class
            getCommand("clearbal").setExecutor(new ClearBal(this));
            } catch (Exception e){
                e.printStackTrace();
                con.severe("An error occurred on registry: " + e);
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

        // Done :)
        con.info("Plugin Enabled");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        con.warn("Plugin Disabled");
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
     * Returns an offline player
     * First scans local offline players
     * @param name - name to scan for.
     * @param allowFetch - Uses deprecated "bukkit.getOfflinePlayer", not reccommended.
     * @return OfflinePlayer - the player corresponding to the name.
     */
    public OfflinePlayer getOfflinePlayer(String name, boolean allowFetch) {
        OfflinePlayer[] oPlayers = getServer().getOfflinePlayers();
        OfflinePlayer player = null;
        for (OfflinePlayer oPlayer : oPlayers) {
            if (oPlayer.getName() == name) {
                player = oPlayer;
                break;
            }
        }

        if (allowFetch == true && player == null) {
            player = getServer().getOfflinePlayer(name);
        }

        return player;
    }

}