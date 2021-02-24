package EDGRRRR.DCE.Main;

import EDGRRRR.DCE.Commands.*;
import EDGRRRR.DCE.Economy.EconomyManager;
import EDGRRRR.DCE.Events.MailEvent;
import EDGRRRR.DCE.Mail.MailManager;
import EDGRRRR.DCE.Materials.MaterialManager;
import EDGRRRR.DCE.PlayerManager.PlayerInventoryManager;
import EDGRRRR.DCE.PlayerManager.PlayerManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Main Class of the plugin
 * Hooks everything together
 */
public class DCEPlugin extends JavaPlugin {
    // The config
    private ConfigManager configManager;
    // The economy
    private EconomyManager economyManager;
    // The console
    private ConsoleManager consoleManager;
    // The material manager
    private MaterialManager materialManager;
    // The mail manager
    private MailManager mailManager;
    // The player manager
    private PlayerManager playerManager;
    // The player inventory manager
    private PlayerInventoryManager playerInventoryManager;

    // Events

    // Handles on-join mail events
    private MailEvent mailEvent;
    // A simple ping command
    private CommandExecutor pingCommand;

    // Fetches and prints user UUIDS for debugging
    // private UUIDFetchEvent uuidFetchEvent;
    // public UUIDFetchEvent getUuidFetchEvent() {return this.uuidFetchEvent;}

    // Commands
    // A command for getting the balance of a user
    private CommandExecutor balanceCommand;
    // An admin command for adding and removing cash from accounts
    private CommandExecutor editbalCommand;
    // A command for sending cash between users
    private CommandExecutor sendcashCommand;
    // A command for setting the balance of an account
    private CommandExecutor setbalCommand;
    // A command for clearing the balance of a user
    private CommandExecutor clearbalCommand;
    // A command for buying items from the market
    private CommandExecutor buyItemCommand;
    // A command for selling items from the market
    private CommandExecutor sellItemCommand;
    // A command for valuing items from the market
    private CommandExecutor valueCommand;
    // A command for getting item information from the market
    private CommandExecutor infoCommand;
    // A command for selling items in hand
    private CommandExecutor handSellCommand;
    // A command for buying items in hand
    private CommandExecutor handBuyCommand;

    public MailEvent getMailEvent() {
        return this.mailEvent;
    }

    public CommandExecutor getCommandPing() {
        return this.pingCommand;
    }

    public CommandExecutor getCommandBalance() {
        return this.balanceCommand;
    }

    public CommandExecutor getCommandEditBal() {
        return this.editbalCommand;
    }

    public CommandExecutor getCommandSendCash() {
        return this.sendcashCommand;
    }

    public CommandExecutor getCommandSetBal() {
        return this.setbalCommand;
    }

    public CommandExecutor getCommandClearBal() {
        return this.clearbalCommand;
    }

    public CommandExecutor getCommandBuyItem() {
        return this.buyItemCommand;
    }

    public CommandExecutor getCommandSellItem() {
        return this.sellItemCommand;
    }

    public CommandExecutor getCommandValue() {
        return this.valueCommand;
    }

    public CommandExecutor getCommandInfo() {
        return this.infoCommand;
    }

    public CommandExecutor getCommandHandSell() {
        return this.handSellCommand;
    }

    public CommandExecutor getCommandHandBuy() {
        return this.handBuyCommand;
    }

    /**
     * Called when the plugin is enabled
     * Setup console
     * Setup Economy
     */
    @Override
    public void onEnable() {
        // Config
        this.configManager = new ConfigManager(this);
        //Setup Managers
        this.consoleManager = new ConsoleManager(this);
        this.economyManager = new EconomyManager(this);
        this.economyManager.setupEconomy();
        this.materialManager = new MaterialManager(this);
        this.materialManager.loadAliases();
        this.materialManager.loadMaterials();
        this.playerManager = new PlayerManager(this);
        this.playerInventoryManager = new PlayerInventoryManager(this);
        this.mailManager = new MailManager(this);
        this.mailManager.setupMailFile();
        this.mailManager.loadAllMail();

        // setup events
        try {
            // Create events
            // this.UUIDFetchEvent = new UUIDFetchEvent(this);
            this.mailEvent = new MailEvent(this);

            // Register events
            PluginManager pm = this.getServer().getPluginManager();
            // pm.registerEvents(this.getUuidFetchEvent(), this);
            pm.registerEvents(this.getMailEvent(), this);
        } catch (Exception e) {
            e.printStackTrace();
            this.consoleManager.severe("An error occurred on event creation: " + e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }


        // setup commands
        this.pingCommand = new Ping(this);
        this.balanceCommand = new Balance(this);
        this.editbalCommand = new EditBal(this);
        this.sendcashCommand = new SendCash(this);
        this.setbalCommand = new SetBal(this);
        this.clearbalCommand = new ClearBal(this);
        this.buyItemCommand = new BuyItem(this);
        this.sellItemCommand = new SellItem(this);
        this.valueCommand = new Value(this);
        this.infoCommand = new Info(this);
        this.handSellCommand = new HandSell(this);
        this.handBuyCommand = new HandBuy(this);

        try {
            // Register commands
            this.getCommand("ping").setExecutor(this.pingCommand);
            this.getCommand("balance").setExecutor(this.balanceCommand);
            this.getCommand("editbal").setExecutor(this.editbalCommand);
            this.getCommand("sendcash").setExecutor(this.sendcashCommand);
            this.getCommand("setbal").setExecutor(this.setbalCommand);
            this.getCommand("clearbal").setExecutor(this.clearbalCommand);
            this.getCommand("buy").setExecutor(this.buyItemCommand);
            this.getCommand("sell").setExecutor(this.sellItemCommand);
            this.getCommand("value").setExecutor(this.valueCommand);
            this.getCommand("information").setExecutor(this.infoCommand);
            this.getCommand("handSell").setExecutor(this.handSellCommand);
            this.getCommand("handBuy").setExecutor(this.handBuyCommand);
        } catch (Exception e) {
            e.printStackTrace();
            this.consoleManager.severe("An error occurred on registry: " + e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Done :)
        this.describe();
        this.consoleManager.info("Plugin Enabled");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        if (!(this.materialManager == null)) {
            this.materialManager.saveAll();
        }
        if (!(this.mailManager == null)) {
            this.mailManager.saveAllMail();
        }
        this.consoleManager.warn("Plugin Disabled");
    }

    public void describe() {
        this.consoleManager.debug("Materials: " + this.materialManager.materials.size());
        this.consoleManager.debug("Aliases: " + this.materialManager.aliases.size());
        this.consoleManager.debug("Starting Items: " + this.materialManager.baseTotalMaterials);
        this.consoleManager.debug("Actual Items: " + this.materialManager.totalMaterials);
        this.consoleManager.debug("Inflation: " + this.materialManager.getInflation() + "%");
    }

    /**
     * Returns the economy manager
     *
     * @return EconomyManager
     */
    public EconomyManager getEconomyManager() {
        return this.economyManager;
    }

    /**
     * Returns the console
     *
     * @return ConsoleManager
     */
    public ConsoleManager getConsoleManager() {
        return this.consoleManager;
    }

    /**
     * Returns the config manager
     *
     * @return ConfigManager
     */
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    /**
     * Returns the Material Manager
     *
     * @return MaterialManager
     */
    public MaterialManager getMaterialManager() {
        return this.materialManager;
    }

    /**
     * Returns the mail manager
     *
     * @return MailManager
     */
    public MailManager getMailManager() {
        return this.mailManager;
    }

    /**
     * Returns the player manager
     *
     * @return PlayerManager
     */
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public PlayerInventoryManager getPlayerInventoryManager() {
        return this.playerInventoryManager;
    }
}
