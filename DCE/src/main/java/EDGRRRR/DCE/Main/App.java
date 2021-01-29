package EDGRRRR.DCE.Main;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
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
    // The config
    private ConfigM conf;
    // The economy
    private EconomyM eco;
    // The console
    private Console con;

    // Commands

    // A simple ping command
    private CommandExecutor ping;
    public CommandExecutor getCommandPing() { return this.ping; }

    // A command for getting the balance of a user
    private CommandExecutor balance;
    public CommandExecutor getCommandBalance() { return this.balance; }

    // An admin command for adding and removing cash from accounts
    private CommandExecutor editbal;
    public CommandExecutor getCommandEditBal() { return this.editbal; }

    // A command for sending cash between users
    private CommandExecutor sendcash;
    public CommandExecutor getCommandSendCash() { return this.sendcash; }

    // A command for setting the balance of an account
    private CommandExecutor setbal;
    public CommandExecutor getCommandSetBal() { return this.setbal; }

    // A command for clearing the balance of a user
    private CommandExecutor clearbal;
    public CommandExecutor getCommandClearBal() { return this.clearbal; }

    /**
     * Called when the plugin is enabled
     * Setup console
     * Setup Economy
     */
    @Override
    public void onEnable() {
        // Config
        this.conf = new ConfigM(this);
        //Setup Managers
        this.con = new Console(this);
    	this.eco = new EconomyM(this);
        this.eco.setupEconomy();
        // setup commands
        this.ping = new Ping(this);
        this.balance = new Balance(this);
        this.editbal = new EditBal(this);
        this.sendcash = new SendCash(this);
        this.setbal = new SetBal(this);
        this.clearbal = new ClearBal(this);
    	
    	try {
            // Register commands
            getCommand("ping").setExecutor(this.ping);
            getCommand("balance").setExecutor(this.balance);
            getCommand("editbal").setExecutor(this.editbal);
            getCommand("sendcash").setExecutor(this.sendcash);
            getCommand("setbal").setExecutor(this.setbal);
            getCommand("clearbal").setExecutor(this.clearbal);
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
        return this.eco;
    }

    /**
     * Returns the console
     * @return Console
     */
    public Console getCon() {
        return this.con;
    }

    /**
     * Returns the config
     * @return ConfigM
     */
    public ConfigM getConf() {
        return this.conf;
    }

    /**
     * Returns an offline player
     * First scans local offline players
     * @param name - name to scan for.
     * @param allowFetch - Uses deprecated "bukkit.getOfflinePlayer", not reccommended.
     * @return OfflinePlayer - the player corresponding to the name.
     */
    public OfflinePlayer getOfflinePlayer(String name, boolean allowFetch) {
        name = name.trim().toLowerCase();
        OfflinePlayer[] oPlayers = getServer().getOfflinePlayers();
        OfflinePlayer player = null;
        for (OfflinePlayer oPlayer : oPlayers) {
            String oPlayerName = oPlayer.getName().trim().toLowerCase();
            if (oPlayerName.equals(name)) {
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