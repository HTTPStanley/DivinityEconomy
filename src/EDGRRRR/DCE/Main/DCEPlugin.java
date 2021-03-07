package edgrrrr.dce.main;

import edgrrrr.dce.commands.admin.ClearBal;
import edgrrrr.dce.commands.admin.EditBal;
import edgrrrr.dce.commands.admin.SetBal;
import edgrrrr.dce.commands.enchants.EnchantHandSell;
import edgrrrr.dce.commands.mail.ClearMail;
import edgrrrr.dce.commands.mail.ReadMail;
import edgrrrr.dce.commands.market.*;
import edgrrrr.dce.commands.misc.Ping;
import edgrrrr.dce.commands.money.Balance;
import edgrrrr.dce.commands.money.SendCash;
import edgrrrr.dce.config.ConfigManager;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.economy.EconomyManager;
import edgrrrr.dce.enchants.EnchantmentManager;
import edgrrrr.dce.events.MailEvent;
import edgrrrr.dce.mail.MailManager;
import edgrrrr.dce.materials.MaterialManager;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.player.PlayerManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

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
    // The enchantment manager
    private EnchantmentManager enchantmentManager;

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
    //
    private CommandExecutor eHandSellCommand;

    /**
     * Static method for returing the app if there is no dependancy injection for the caller.
     * @return
     */
    public static DCEPlugin getApp() {
        return app;
    }

    /**
     * Returns the mail event
     * @return MailEvent
     */
    public MailEvent getMailEvent() {
        return this.mailEvent;
    }

    /**
     * Returns the ping command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandPing() {
        return this.pingCommand;
    }

    /**
     * Returns the get balance command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandBalance() {
        return this.balanceCommand;
    }

    /**
     * Returns the edit bal command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandEditBal() {
        return this.editbalCommand;
    }

    /**
     * Returns the send cash command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandSendCash() {
        return this.sendcashCommand;
    }

    /**
     * Returns the set bal command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandSetBal() {
        return this.setbalCommand;
    }

    /**
     * Returns the clear bal command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandClearBal() {
        return this.clearbalCommand;
    }

    /**
     * Returns the buy item command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandBuyItem() {
        return this.buyItemCommand;
    }

    /**
     * Returns the sell item command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandSellItem() {
        return this.sellItemCommand;
    }

    /**
     * Returns the value command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandValue() {
        return this.valueCommand;
    }

    /**
     * Returns the info command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandInfo() {
        return this.infoCommand;
    }

    /**
     * Returns the hand sell command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandHandSell() {
        return this.handSellCommand;
    }

    /**
     * Returns the hand buy command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandHandBuy() {
        return this.handBuyCommand;
    }

    /**
     * Returns the hand value command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandHandValue() {
        return this.handValueCommand;
    }

    /**
     * Returns the hand info command executor
     * @return CommandExecutor
     */
    public CommandExecutor getCommandHandInfo() {
        return this.handInfoCommand;
    }

    /**
     * Returns the read mail command executor
     * @return CommandExecutor
     */
    public CommandExecutor getReadMailCommand() {
        return this.readMailCommand;
    }

    /**
     * Returns the mail command executor
     * @return CommandExecutor
     */
    public CommandExecutor getClearMailCommand() {
        return this.clearMailCommand;
    }

    public CommandExecutor getEHandSellCommand() {return this.eHandSellCommand;}

    /**
     * Called when the plugin is enabled
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
        this.enchantmentManager = new EnchantmentManager(this);
        this.enchantmentManager.loadEnchants();
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
        this.readMailCommand = new ReadMail(this);
        this.clearMailCommand = new ClearMail(this);
        this.eHandSellCommand = new EnchantHandSell(this);

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
            this.getCommand("eHandSell").setExecutor(this.getEHandSellCommand());
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
        if (this.materialManager != null) {
            this.materialManager.saveMaterials();
        }
        if (this.enchantmentManager != null) {
            this.enchantmentManager.saveEnchants();
        }
        if (this.mailManager != null) {
            this.mailManager.saveAllMail();
        }
        this.consoleManager.warn("Plugin Disabled");
    }

    /**
     * A debug command that prints information about the plugin
     * Such as settings, the materials market variables, the enchant market variables.
     */
    public void describe() {
        Setting[] chatSettings = {Setting.CHAT_DEBUG_OUTPUT_BOOLEAN, Setting.CHAT_PREFIX_STRING, Setting.CHAT_CONSOLE_PREFIX,
                Setting.CHAT_PREFIX_COLOR, Setting.CHAT_PREFIX_SEPARATOR_STRING, Setting.CHAT_PREFIX_SEPARATOR_COLOR,
                Setting.CHAT_INFO_COLOR, Setting.CHAT_WARNING_COLOR, Setting.CHAT_SEVERE_COLOR, Setting.CHAT_DEBUG_COLOR};

        Setting[] economySettings = {Setting.ECONOMY_MIN_SEND_AMOUNT_DOUBLE, Setting.ECONOMY_ACCURACY_DIGITS_INTEGER, Setting.ECONOMY_MIN_BALANCE_DOUBLE};

        Setting[] marketSettings = {Setting.MARKET_SAVE_TIMER_INTEGER};

        Setting[] materialSettings = {Setting.MARKET_MATERIALS_ENABLE_BOOLEAN, Setting.MARKET_MATERIALS_BASE_QUANTITY_INTEGER, Setting.MARKET_MATERIALS_BUY_TAX_FLOAT,
                Setting.MARKET_MATERIALS_SELL_TAX_FLOAT};

        Setting[] enchantSettings = {Setting.MARKET_ENCHANTS_ENABLE_BOOLEAN, Setting.MARKET_ENCHANTS_BASE_QUANTITY_INTEGER, Setting.MARKET_ENCHANTS_BUY_TAX_FLOAT,
                Setting.MARKET_ENCHANTS_SELL_TAX_FLOAT};

        Setting[] commandSettings = {Setting.COMMAND_PING_ENABLE_BOOLEAN, Setting.COMMAND_BALANCE_ENABLE_BOOLEAN, Setting.COMMAND_SET_BALANCE_ENABLE_BOOLEAN, Setting.COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN,
                Setting.COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN, Setting.COMMAND_BALANCE_ENABLE_BOOLEAN, Setting.COMMAND_SEND_CASH_ENABLE_BOOLEAN, Setting.COMMAND_BUY_ITEM_ENABLE_BOOLEAN, Setting.COMMAND_SELL_ITEM_ENABLE_BOOLEAN,
                Setting.COMMAND_HAND_SELL_ITEM_ENABLE_BOOLEAN, Setting.COMMAND_HAND_BUY_ITEM_ENABLE_BOOLEAN, Setting.COMMAND_VALUE_ENABLE_BOOLEAN, Setting.COMMAND_HAND_VALUE_ENABLE_BOOLEAN, Setting.COMMAND_SEARCH_ENABLE_BOOLEAN,
                Setting.COMMAND_INFO_ENABLE_BOOLEAN, Setting.COMMAND_HAND_INFO_ENABLE_BOOLEAN, Setting.COMMAND_READ_MAIL_ENABLE_BOOLEAN, Setting.COMMAND_CLEAR_MAIL_ENABLE_BOOLEAN, Setting.COMMAND_E_HAND_SELL_ENABLE_BOOLEAN,
                Setting.COMMAND_E_HAND_BUY_ENABLE_BOOLEAN, Setting.COMMAND_E_SELL_ENABLE_BOOLEAN, Setting.COMMAND_E_BUY_ENABLE_BOOLEAN, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN, Setting.COMMAND_E_HAND_VALUE_ENABLE_BOOLEAN,
                Setting.COMMAND_E_INFO_ENABLE_BOOLEAN, Setting.COMMAND_E_HAND_INFO_ENABLE_BOOLEAN};

        HashMap<String, Setting[]> settingMap = new HashMap<>();
        settingMap.put("Chat", chatSettings);
        settingMap.put("Economy", economySettings);
        settingMap.put("Market", marketSettings);
        settingMap.put("   Materials", materialSettings);
        settingMap.put("   Enchants", enchantSettings);
        settingMap.put("Commands", commandSettings);


        this.consoleManager.debug("===Describe===");
        this.consoleManager.debug("Settings:");
        for (String settingGroup : settingMap.keySet()) {
            this.consoleManager.debug(String.format("   %s:", settingGroup));
            for (Setting setting : settingMap.get(settingGroup)) {
                this.consoleManager.debug(String.format("      - %s: %s", setting.path(), this.getConfig().getString(setting.path())));
            }
            this.consoleManager.debug("");
        }
        this.consoleManager.debug("");
        this.consoleManager.debug("Markets:");
        this.consoleManager.debug("   - Materials: " + this.materialManager.materials.size());
        this.consoleManager.debug("      - Material Aliases: " + this.materialManager.aliases.size());
        this.consoleManager.debug("      - Material Market Size: " + this.materialManager.getTotalMaterials() + " / " + this.materialManager.getDefaultTotalMaterials());
        this.consoleManager.debug("      - Material Market Inflation: " + this.materialManager.getInflation() + "%");
        this.consoleManager.debug("   - Enchants: " + this.enchantmentManager.enchants.size());
        this.consoleManager.debug("      - Enchant Market Size: " + this.enchantmentManager.getTotalEnchants() + " / " + this.enchantmentManager.getDefaultTotalEnchants());
        this.consoleManager.debug("      - Enchant Market Inflation: " + this.enchantmentManager.getInflation() + "%");
        this.consoleManager.debug("");
    }

    /**
     * Returns the economy manager
     * Handles all Vault API actions. Such as sending, adding, removing and setting cash.
     * @return EconomyManager
     */
    public EconomyManager getEconomyManager() {
        return this.economyManager;
    }

    /**
     * Returns the console
     * This is used for simplifying the interaction with the console, log and players
     * @return ConsoleManager
     */
    public ConsoleManager getConsoleManager() {
        return this.consoleManager;
    }

    /**
     * Returns the config manager
     * This is used for storing the config variables and simple loading/saving functions.
     * @return ConfigManager
     */
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    /**
     * Returns the Material Manager
     * This is used for managing materials and their value.
     * @return MaterialManager
     */
    public MaterialManager getMaterialManager() {
        return this.materialManager;
    }

    /**
     * Returns the mail manager
     * Used to getting, creating and setting Mail for, mostly offline, users.
     * @return MailManager
     */
    public MailManager getMailManager() {
        return this.mailManager;
    }

    /**
     * Returns the player manager
     * This is currently used for getting Player and OfflinePlayer objects
     * @return PlayerManager
     */
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    /**
     * Returns the player inventory manager
     * This is used for handling a players inventory and materials within it.
     * @return
     */
    public PlayerInventoryManager getPlayerInventoryManager() {
        return this.playerInventoryManager;
    }

    /**
     * Returns the enchantment manager
     * This is used for handling enchantments on items and determining their value.
     * @return
     */
    public EnchantmentManager getEnchantmentManager() { return this.enchantmentManager; }
}
