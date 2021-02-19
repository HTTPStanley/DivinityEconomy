package EDGRRRR.DCE.Main;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import EDGRRRR.DCE.Commands.Balance;
import EDGRRRR.DCE.Commands.BuyItem;
import EDGRRRR.DCE.Commands.SellItem;
import EDGRRRR.DCE.Commands.ClearBal;
import EDGRRRR.DCE.Commands.EditBal;
import EDGRRRR.DCE.Commands.HandSell;
import EDGRRRR.DCE.Commands.Info;
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

    // A command for selling items from the market
    private CommandExecutor sellItem;
    public CommandExecutor getCommandSellItem() { return this.sellItem; }


    // A command for valuing items from the market
    private CommandExecutor value;
    public CommandExecutor getCommandValue() { return this.value; }

    // A command for getting item information from the market
    private CommandExecutor info;
    public CommandExecutor getCommandInfo() { return this.info; }

    // A command for selling items in hand
    private CommandExecutor handSell;
    public CommandExecutor getCommandHandSell() { return this.handSell; }

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
        this.sellItem = new SellItem(this);
        this.value = new Value(this);
        this.info = new Info(this);
        this.handSell = new HandSell(this);

    	try {
            // Register commands
            this.getCommand("ping").setExecutor(this.ping);
            this.getCommand("balance").setExecutor(this.balance);
            this.getCommand("editbal").setExecutor(this.editbal);
            this.getCommand("sendcash").setExecutor(this.sendcash);
            this.getCommand("setbal").setExecutor(this.setbal);
            this.getCommand("clearbal").setExecutor(this.clearbal);
            this.getCommand("buy").setExecutor(this.buyItem);
            this.getCommand("sell").setExecutor(this.sellItem);
            this.getCommand("value").setExecutor(this.value);
            this.getCommand("information").setExecutor(this.info);
            this.getCommand("handSell").setExecutor(this.handSell);
            } catch (Exception e){
                e.printStackTrace();
                this.con.severe("An error occurred on registry: " + e);
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }

        // Done :)
        this.describe();
        this.con.info("Plugin Enabled");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        if (!(this.mat == null)) {
            this.mat.saveAll();
        }
        this.con.warn("Plugin Disabled");
    }

    public void describe() {
        this.con.debug("Materials: " + this.mat.materials.size());
        this.con.debug("Aliases: " + this.mat.aliases.size());
        this.con.debug("Starting Items: " + this.mat.baseTotalMaterials);
        this.con.debug("Actual Items: " + this.mat.totalMaterials);
        this.con.debug("Inflation: " + this.eco.round(this.mat.getInflation(),2) + "%");
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
            player = this.getServer().getOfflinePlayer(name);
        }

        return player;
    }

}