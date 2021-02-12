package EDGRRRR.DCE.Main;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import EDGRRRR.DCE.Commands.Balance;
import EDGRRRR.DCE.Commands.BuyItem;
import EDGRRRR.DCE.Commands.ClearBal;
import EDGRRRR.DCE.Commands.EditBal;
import EDGRRRR.DCE.Commands.Ping;
import EDGRRRR.DCE.Commands.SendCash;
import EDGRRRR.DCE.Commands.SetBal;
import EDGRRRR.DCE.Commands.Value;
import EDGRRRR.DCE.Economy.Materials.MaterialManager;
import EDGRRRR.DCE.Economy.EconomyManager;
/**
 * The Main Class of the plugin
 * Hooks everything together
 */
public class DCEPlugin extends JavaPlugin {
    // The config
    private ConfigManager conf;
    // The economy
    private EconomyManager eco;
    // The console
    private ConsoleManager con;
    // The material manager
    private MaterialManager mat;

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

    // A command for buying items from the market
    private CommandExecutor buyItem;
    public CommandExecutor getCommandBuyItem() { return this.buyItem; }


    // A command for buying items from the market
    private CommandExecutor value;
    public CommandExecutor getCommandValue() { return this.value; }

    /**
     * Called when the plugin is enabled
     * Setup console
     * Setup Economy
     */
    @Override
    public void onEnable() {
        // Config
        this.conf = new ConfigManager(this);
        //Setup Managers
        this.con = new ConsoleManager(this);
    	this.eco = new EconomyManager(this);
        this.eco.setupEconomy();
        this.mat = new MaterialManager(this);
        this.mat.loadAliases();
        this.mat.loadMaterials();
        // setup commands
        this.ping = new Ping(this);
        this.balance = new Balance(this);
        this.editbal = new EditBal(this);
        this.sendcash = new SendCash(this);
        this.setbal = new SetBal(this);
        this.clearbal = new ClearBal(this);
        this.buyItem = new BuyItem(this);
        this.value = new Value(this);
    	
    	try {
            // Register commands
            getCommand("ping").setExecutor(this.ping);
            getCommand("balance").setExecutor(this.balance);
            getCommand("editbal").setExecutor(this.editbal);
            getCommand("sendcash").setExecutor(this.sendcash);
            getCommand("setbal").setExecutor(this.setbal);
            getCommand("clearbal").setExecutor(this.clearbal);
            getCommand("buy").setExecutor(this.buyItem);
            getCommand("value").setExecutor(this.value);
            } catch (Exception e){
                e.printStackTrace();
                con.severe("An error occurred on registry: " + e);
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

        // Done :)
        describe();
        con.info("Plugin Enabled");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        if (!(mat == null)) {
            mat.saveAll();
        }
        con.warn("Plugin Disabled");
    }

    public void describe() {
        con.debug("Materials: " + mat.materials.size());
        con.debug("Aliases: " + mat.aliases.size());
        con.debug("Starting Items: " + mat.baseTotalMaterials);
        con.debug("Actual Items: " + mat.totalMaterials);
        con.debug("Inflation: " + eco.round(mat.getInflation(),2) + "%");
    }

    /**
     * Returns the economy
     * @return Economy
     */
    public EconomyManager getEco() {
        return this.eco;
    }

    /**
     * Returns the console
     * @return Console
     */
    public ConsoleManager getCon() {
        return this.con;
    }

    /**
     * Returns the config
     * @return ConfigM
     */
    public ConfigManager getConf() {
        return this.conf;
    }

    /**
     * Returns the Material Manager
     * @return MaterialM
     */
    public MaterialManager getMat() {
        return this.mat;
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