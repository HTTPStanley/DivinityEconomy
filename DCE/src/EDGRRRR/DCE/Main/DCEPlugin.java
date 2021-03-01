package EDGRRRR.DCE.Main;

import EDGRRRR.DCE.Commands.Admin.ClearBal;
import EDGRRRR.DCE.Commands.Admin.EditBal;
import EDGRRRR.DCE.Commands.Admin.SetBal;
import EDGRRRR.DCE.Commands.Mail.clearMail;
import EDGRRRR.DCE.Commands.Mail.readMail;
import EDGRRRR.DCE.Commands.Money.Balance;
import EDGRRRR.DCE.Commands.Money.SendCash;
import EDGRRRR.DCE.Commands.Market.*;
import EDGRRRR.DCE.Commands.Misc.Ping;
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
    private static DCEPlugin app;
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
    // A command for getting the value of the item you're holding
    private CommandExecutor handValueCommand;
    // A command for getting the information of the item you're holding
    private CommandExecutor handInfoCommand;
    // A command for getting the mail list of a player
    private CommandExecutor readMailCommand;
    // A command for clearing the mail list of a player
    private CommandExecutor clearMailCommand;

    public static DCEPlugin getApp() {
        return app;
    }

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

    public CommandExecutor getCommandHandValue() { return this.handValueCommand; }

    public CommandExecutor getCommandHandInfo() { return this.handInfoCommand; }

    public CommandExecutor getReadMailCommand() { return this.readMailCommand; }

    public CommandExecutor getClearMailCommand() { return this.clearMailCommand; }

    /**
     * Called when the plugin is enabled
     * Setup console
     * Setup Economy
     */
    @Override
    public void onEnable() {
        app = this;
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
        this.handValueCommand = new HandValue(this);
        this.handInfoCommand = new HandInfo(this);
        this.readMailCommand = new readMail(this);
        this.clearMailCommand = new clearMail(this);

        try {
            // Register commands
            this.getCommand("ping").setExecutor(this.getCommandPing());
            this.getCommand("balance").setExecutor(this.getCommandBalance());
            this.getCommand("editbal").setExecutor(this.getCommandEditBal());
            this.getCommand("sendcash").setExecutor(this.getCommandSendCash());
            this.getCommand("setbal").setExecutor(this.getCommandSetBal());
            this.getCommand("clearbal").setExecutor(this.getCommandClearBal());
            this.getCommand("buy").setExecutor(this.getCommandBuyItem());
            this.getCommand("sell").setExecutor(this.getCommandSellItem());
            this.getCommand("value").setExecutor(this.getCommandValue());
            this.getCommand("information").setExecutor(this.getCommandInfo());
            this.getCommand("handSell").setExecutor(this.getCommandHandSell());
            this.getCommand("handBuy").setExecutor(this.getCommandHandBuy());
            this.getCommand("handValue").setExecutor(this.getCommandHandValue());
            this.getCommand("handInformation").setExecutor(this.getCommandHandInfo());
            this.getCommand("readMail").setExecutor(this.getReadMailCommand());
            this.getCommand("clearMail").setExecutor(this.getClearMailCommand());
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
        this.consoleManager.debug("Market Size: " + this.materialManager.totalMaterials + " / " + this.materialManager.baseTotalMaterials);
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
