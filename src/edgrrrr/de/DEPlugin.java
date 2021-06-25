package edgrrrr.de;

import edgrrrr.de.commands.admin.*;
import edgrrrr.de.commands.enchants.*;
import edgrrrr.de.commands.help.HelpCommand;
import edgrrrr.de.commands.help.HelpCommandTC;
import edgrrrr.de.commands.mail.ClearMail;
import edgrrrr.de.commands.mail.ClearMailTC;
import edgrrrr.de.commands.mail.ReadMail;
import edgrrrr.de.commands.mail.ReadMailTC;
import edgrrrr.de.commands.market.*;
import edgrrrr.de.commands.misc.Ping;
import edgrrrr.de.commands.money.Balance;
import edgrrrr.de.commands.money.BalanceTC;
import edgrrrr.de.commands.money.SendCash;
import edgrrrr.de.commands.money.SendCashTC;
import edgrrrr.de.config.ConfigManager;
import edgrrrr.de.config.Setting;
import edgrrrr.de.console.EconConsole;
import edgrrrr.de.console.LogLevel;
import edgrrrr.de.economy.EconomyManager;
import edgrrrr.de.enchants.EnchantmentManager;
import edgrrrr.de.events.MailEvent;
import edgrrrr.de.help.HelpManager;
import edgrrrr.de.mail.MailManager;
import edgrrrr.de.materials.MaterialManager;
import edgrrrr.de.player.PlayerManager;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The Main Class of the plugin
 * Hooks everything together
 */
public class DEPlugin extends JavaPlugin {
    // The config
    private ConfigManager config;
    // The console
    private EconConsole console;
    // The economy
    private EconomyManager economyManager;
    // The material manager
    private MaterialManager materialManager;
    // The mail manager
    private MailManager mailManager;
    // The player manager
    private PlayerManager playerManager;
    // The enchantment manager
    private EnchantmentManager enchantmentManager;
    // The help manager
    private HelpManager helpManager;

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        // Config
        LogLevel.loadValuesFromConfig((YamlConfiguration) this.getConfig());
        this.config = new ConfigManager(this);
        this.console = new EconConsole(this);
        this.economyManager = new EconomyManager(this);
        this.materialManager = new MaterialManager(this);
        this.enchantmentManager = new EnchantmentManager(this);
        this.playerManager = new PlayerManager(this);
        this.mailManager = new MailManager(this);
        this.helpManager = new HelpManager(this);

        DivinityModule.runInit();

        this.materialManager.loadAliases();
        this.materialManager.loadMaterials();

        // setup events
        try {
            // Register events
            PluginManager pm = this.getServer().getPluginManager();
            // pm.registerEvents(this.getUuidFetchEvent(), this);
            pm.registerEvents(new MailEvent(this), this);
        } catch (Exception e) {
            e.printStackTrace();
            this.console.severe("An error occurred on event creation: " + e);
            this.shutdown();
            return;
        }

        try {
            // Register commands
            this.getCommand("ping").setExecutor(new Ping(this));

            this.getCommand("balance").setExecutor(new Balance(this));
            this.getCommand("balance").setTabCompleter(new BalanceTC(this));

            this.getCommand("editbal").setExecutor(new EditBal(this));
            this.getCommand("editbal").setTabCompleter(new EditBalTC(this));

            this.getCommand("sendcash").setExecutor(new SendCash(this));
            this.getCommand("sendcash").setTabCompleter(new SendCashTC(this));

            this.getCommand("setbal").setExecutor(new SetBal(this));
            this.getCommand("setbal").setTabCompleter(new SetBalTC(this));

            this.getCommand("clearbal").setExecutor(new ClearBal(this));
            this.getCommand("clearbal").setTabCompleter(new ClearBalTC(this));

            this.getCommand("buy").setExecutor(new Buy(this));
            this.getCommand("buy").setTabCompleter(new BuyTC(this));

            this.getCommand("sell").setExecutor(new Sell(this));
            this.getCommand("sell").setTabCompleter(new SellTC(this));

            this.getCommand("value").setExecutor(new Value(this));
            this.getCommand("value").setTabCompleter(new ValueTC(this));

            this.getCommand("information").setExecutor(new Info(this));
            this.getCommand("information").setTabCompleter(new InfoTC(this));

            this.getCommand("handSell").setExecutor(new HandSell(this));
            this.getCommand("handSell").setTabCompleter(new HandSellTC(this));

            this.getCommand("handBuy").setExecutor(new HandBuy(this));
            this.getCommand("handBuy").setTabCompleter(new HandBuyTC(this));

            this.getCommand("handValue").setExecutor(new HandValue(this));
            this.getCommand("handValue").setTabCompleter(new HandValueTC(this));

            this.getCommand("handInformation").setExecutor(new HandInfo(this));

            this.getCommand("readMail").setExecutor(new ReadMail(this));
            this.getCommand("readMail").setTabCompleter(new ReadMailTC(this));

            this.getCommand("clearMail").setExecutor(new ClearMail(this));
            this.getCommand("clearMail").setTabCompleter(new ClearMailTC(this));

            this.getCommand("eSell").setExecutor(new EnchantHandSell(this));
            this.getCommand("eSell").setTabCompleter(new EnchantHandSellTC(this));

            this.getCommand("eValue").setExecutor(new EnchantValue(this));
            this.getCommand("eValue").setTabCompleter(new EnchantValueTC(this));

            this.getCommand("eHandValue").setExecutor(new EnchantHandValue(this));
            this.getCommand("eHandValue").setTabCompleter(new EnchantHandValueTC(this));

            this.getCommand("eBuy").setExecutor(new EnchantHandBuy(this));
            this.getCommand("eBuy").setTabCompleter(new EnchantHandBuyTC(this));

            this.getCommand("eInfo").setExecutor(new EnchantInfo(this));
            this.getCommand("eInfo").setTabCompleter(new EnchantInfoTC(this));

            this.getCommand("reloadMaterials").setExecutor(new ReloadMaterials(this));

            this.getCommand("reloadEnchants").setExecutor(new ReloadEnchants(this));

            this.getCommand("saveMaterials").setExecutor(new SaveMaterials(this));

            this.getCommand("saveEnchants").setExecutor(new SaveEnchants(this));

            this.getCommand("setStock").setExecutor(new SetStock(this));
            this.getCommand("setStock").setTabCompleter(new SetStockTC(this));

            this.getCommand("setValue").setExecutor(new SetValue(this));
            this.getCommand("setValue").setTabCompleter(new SetValueTC(this));

            this.getCommand("eSetStock").setExecutor(new ESetStock(this));
            this.getCommand("eSetStock").setTabCompleter(new ESetStockTC(this));

            this.getCommand("eSetValue").setExecutor(new ESetValue(this));
            this.getCommand("eSetValue").setTabCompleter(new ESetValueTC(this));

            this.getCommand("ehelp").setExecutor(new HelpCommand(this));
            this.getCommand("ehelp").setTabCompleter(new HelpCommandTC(this));

        } catch (Exception e) {
            e.printStackTrace();
            this.console.severe("An error occurred on registry: " + e);
            this.shutdown();
            return;
        }

        // Done :)
        this.describe();
        this.console.info("Plugin Enabled");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        DivinityModule.runDeinit();
        this.console.warn("Plugin Disabled");
    }

    /**
     * Shorthand for disabling the plugin.
     */
    public void shutdown() {
        this.getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * A debug command that prints information about the plugin
     * Such as settings, the materials market variables, the enchant market variables.
     */
    public void describe() {
        this.console.debug("===Describe===");
        this.console.debug("Settings:");
        for (Setting setting : Setting.values()) {
            Object value = this.getConfigManager().get(setting);
            if (!(value instanceof MemorySection)) this.getConsole().debug(String.format("   - %s: '%s'", setting.path, value));
        }
        this.console.debug("");
        this.console.debug("Markets:");
        this.console.debug("   - Materials: " + this.materialManager.materials.size());
        this.console.debug("      - Material Market Size: " + this.materialManager.getTotalMaterials() + " / " + this.materialManager.getDefaultTotalMaterials());
        this.console.debug("      - Material Market Inflation: " + this.materialManager.getInflation() + "%");
        this.console.debug("   - Enchants: " + this.enchantmentManager.enchants.size());
        this.console.debug("      - Enchant Market Size: " + this.enchantmentManager.getTotalEnchants() + " / " + this.enchantmentManager.getDefaultTotalEnchants());
        this.console.debug("      - Enchant Market Inflation: " + this.enchantmentManager.getInflation() + "%");
        this.console.debug("");
    }

    public ConfigManager getConfigManager() {
        return this.config;
    }

    public EconConsole getConsole() {
        return this.console;
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
     * Returns the enchantment manager
     * This is used for handling enchantments on items and determining their value.
     * @return EnchantmentManager
     */
    public EnchantmentManager getEnchantmentManager() { return this.enchantmentManager; }

    public HelpManager getHelpManager() {return this.helpManager;}
}
