package EDGRRRR.DCE.Main;

import EDGRRRR.DCE.Commands.Admin.ClearBal;
import EDGRRRR.DCE.Commands.Admin.EditBal;
import EDGRRRR.DCE.Commands.Admin.SetBal;
import EDGRRRR.DCE.Commands.Mail.ClearMail;
import EDGRRRR.DCE.Commands.Mail.ReadMail;
import EDGRRRR.DCE.Commands.Market.*;
import EDGRRRR.DCE.Commands.Misc.Ping;
import EDGRRRR.DCE.Commands.Money.Balance;
import EDGRRRR.DCE.Commands.Money.SendCash;
import EDGRRRR.DCE.Economy.EconomyManager;
import EDGRRRR.DCE.Enchants.EnchantmentManager;
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
        this.consoleManager.debug("===Describe===");
        this.consoleManager.debug("Settings:");
        this.consoleManager.debug("   Chat:");
        this.consoleManager.debug("      - " + this.configManager.strChatDebug + ": " + this.getConfig().getString(this.configManager.strChatDebug));
        this.consoleManager.debug("      - " + this.configManager.strChatMsgPfx + ": " + this.getConfig().getString(this.configManager.strChatMsgPfx));
        this.consoleManager.debug("      - " + this.configManager.strChatPfxSep + ": " + this.getConfig().getString(this.configManager.strChatPfxSep));
        this.consoleManager.debug("      - " + this.configManager.strChatPfxClr + ": " + this.getConfig().getString(this.configManager.strChatPfxClr));
        this.consoleManager.debug("      - " + this.configManager.strChatPfxSepClr + ": " + this.getConfig().getString(this.configManager.strChatPfxSepClr));
        this.consoleManager.debug("      - " + this.configManager.strChatConsPfx + ": " + this.getConfig().getString(this.configManager.strChatConsPfx));
        this.consoleManager.debug("      - " + this.configManager.strChatInfClr + ": " + this.getConfig().getString(this.configManager.strChatInfClr));
        this.consoleManager.debug("      - " + this.configManager.strChatWrnClr + ": " + this.getConfig().getString(this.configManager.strChatWrnClr));
        this.consoleManager.debug("      - " + this.configManager.strChatSvrClr + ": " + this.getConfig().getString(this.configManager.strChatSvrClr));
        this.consoleManager.debug("      - " + this.configManager.strChatDbgClr + ": " + this.getConfig().getString(this.configManager.strChatDbgClr));
        this.consoleManager.debug("      - " + this.configManager.strChatConsPfx + ": " + this.getConfig().getString(this.configManager.strChatConsPfx));
        this.consoleManager.debug("");
        this.consoleManager.debug("   Economy:");
        this.consoleManager.debug("      - " + this.configManager.strEconomyMinSendAmount + ": " + this.getConfig().getString(this.configManager.strEconomyMinSendAmount));
        this.consoleManager.debug("      - " + this.configManager.strEconomyRoundingDigits + ": " + this.getConfig().getString(this.configManager.strEconomyRoundingDigits));
        this.consoleManager.debug("      - " + this.configManager.strEconomyMinAccountBalance + ": " + this.getConfig().getString(this.configManager.strEconomyMinAccountBalance));
        this.consoleManager.debug("");
        this.consoleManager.debug("   Market:");
        this.consoleManager.debug("      - " + this.configManager.strMarketSaveTimer + ": " + this.getConfig().getString(this.configManager.strMarketSaveTimer));
        this.consoleManager.debug("");
        this.consoleManager.debug("      Materials:");
        this.consoleManager.debug("         - " + this.configManager.strMaterialEnable + ": " + this.getConfig().getString(this.configManager.strMaterialEnable));
        this.consoleManager.debug("         - " + this.configManager.strMaterialBaseQuantity + ": " + this.getConfig().getString(this.configManager.strMaterialBaseQuantity));
        this.consoleManager.debug("         - " + this.configManager.strMaterialBuyTax + ": " + this.getConfig().getString(this.configManager.strMaterialBuyTax));
        this.consoleManager.debug("         - " + this.configManager.strMaterialSellTax + ": " + this.getConfig().getString(this.configManager.strMaterialSellTax));
        this.consoleManager.debug("      Enchants:");
        this.consoleManager.debug("         - " + this.configManager.strEnchantEnable + ": " + this.getConfig().getString(this.configManager.strEnchantEnable));
        this.consoleManager.debug("         - " + this.configManager.strEnchantBaseQuantity + ": " + this.getConfig().getString(this.configManager.strEnchantBaseQuantity));
        this.consoleManager.debug("         - " + this.configManager.strEnchantBuyTax + ": " + this.getConfig().getString(this.configManager.strEnchantBuyTax));
        this.consoleManager.debug("         - " + this.configManager.strEnchantSellTax + ": " + this.getConfig().getString(this.configManager.strEnchantSellTax));
        this.consoleManager.debug("");
        this.consoleManager.debug("   Commands:");
        this.consoleManager.debug("      - " + this.configManager.strComBalance + ": " + this.getConfig().getString(this.configManager.strComBalance));
        this.consoleManager.debug("      - " + this.configManager.strComClearBal + ": " + this.getConfig().getString(this.configManager.strComClearBal));
        this.consoleManager.debug("      - " + this.configManager.strComEditBal + ": " + this.getConfig().getString(this.configManager.strComEditBal));
        this.consoleManager.debug("      - " + this.configManager.strComPing + ": " + this.getConfig().getString(this.configManager.strComPing));
        this.consoleManager.debug("      - " + this.configManager.strComSendCash + ": " + this.getConfig().getString(this.configManager.strComSendCash));
        this.consoleManager.debug("      - " + this.configManager.strComSetBal + ": " + this.getConfig().getString(this.configManager.strComSetBal));
        this.consoleManager.debug("      - " + this.configManager.strComBuyItem + ": " + this.getConfig().getString(this.configManager.strComBuyItem));
        this.consoleManager.debug("      - " + this.configManager.strComSellItem + ": " + this.getConfig().getString(this.configManager.strComSellItem));
        this.consoleManager.debug("      - " + this.configManager.strComHandSell + ": " + this.getConfig().getString(this.configManager.strComHandSell));
        this.consoleManager.debug("      - " + this.configManager.strComHandBuy + ": " + this.getConfig().getString(this.configManager.strComHandBuy));
        this.consoleManager.debug("      - " + this.configManager.strComValue + ": " + this.getConfig().getString(this.configManager.strComValue));
        this.consoleManager.debug("      - " + this.configManager.strComSearch + ": " + this.getConfig().getString(this.configManager.strComSearch));
        this.consoleManager.debug("      - " + this.configManager.strComInfo + ": " + this.getConfig().getString(this.configManager.strComInfo));
        this.consoleManager.debug("      - " + this.configManager.strComHandInfo + ": " + this.getConfig().getString(this.configManager.strComHandInfo));
        this.consoleManager.debug("      - " + this.configManager.strComHandValue + ": " + this.getConfig().getString(this.configManager.strComHandValue));
        this.consoleManager.debug("      - " + this.configManager.strComReadMail + ": " + this.getConfig().getString(this.configManager.strComReadMail));
        this.consoleManager.debug("      - " + this.configManager.strComClearMail + ": " + this.getConfig().getString(this.configManager.strComClearMail));
        this.consoleManager.debug("");
        this.consoleManager.debug("Markets:");
        this.consoleManager.debug("      - Materials: " + this.materialManager.materials.size());
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
