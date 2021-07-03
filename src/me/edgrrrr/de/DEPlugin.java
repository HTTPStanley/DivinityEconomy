package me.edgrrrr.de;

import me.edgrrrr.de.commands.admin.*;
import me.edgrrrr.de.commands.enchants.*;
import me.edgrrrr.de.commands.help.HelpCommand;
import me.edgrrrr.de.commands.help.HelpCommandTC;
import me.edgrrrr.de.commands.mail.ClearMail;
import me.edgrrrr.de.commands.mail.ClearMailTC;
import me.edgrrrr.de.commands.mail.ReadMail;
import me.edgrrrr.de.commands.mail.ReadMailTC;
import me.edgrrrr.de.commands.market.*;
import me.edgrrrr.de.commands.misc.Ping;
import me.edgrrrr.de.commands.money.Balance;
import me.edgrrrr.de.commands.money.BalanceTC;
import me.edgrrrr.de.commands.money.SendCash;
import me.edgrrrr.de.commands.money.SendCashTC;
import me.edgrrrr.de.config.ConfigManager;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.console.EconConsole;
import me.edgrrrr.de.console.LogLevel;
import me.edgrrrr.de.economy.EconomyManager;
import me.edgrrrr.de.enchants.EnchantmentManager;
import me.edgrrrr.de.events.MailEvent;
import me.edgrrrr.de.help.HelpManager;
import me.edgrrrr.de.mail.MailManager;
import me.edgrrrr.de.materials.MaterialManager;
import me.edgrrrr.de.placeholderAPI.ExpansionManager;
import me.edgrrrr.de.placeholderAPI.expansions.*;
import me.edgrrrr.de.player.PlayerManager;
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
    // The placeholder api expansion manager
    private ExpansionManager expansionManager;

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        // Instantiates all modules
        LogLevel.loadValuesFromConfig((YamlConfiguration) this.getConfig());
        this.config = new ConfigManager(this);
        this.console = new EconConsole(this);
        this.economyManager = new EconomyManager(this);
        this.materialManager = new MaterialManager(this);
        this.enchantmentManager = new EnchantmentManager(this);
        this.playerManager = new PlayerManager(this);
        this.mailManager = new MailManager(this);
        this.helpManager = new HelpManager(this);

        // Initialisation of all modules
        DivinityModule.runInit();

        // setup events
        try {
            // Register events
            PluginManager pm = this.getServer().getPluginManager();
            pm.registerEvents(new MailEvent(this), this);
        } catch (Exception e) {
            e.printStackTrace();
            this.console.severe("An error occurred on event creation: %s", e);
            this.shutdown();
            return;
        }

        // Commands

        // Admin
        new ClearBal(this);
        new ClearBalTC(this);
        new EditBal(this);
        new EditBalTC(this);
        new ESetStock(this);
        new ESetStockTC(this);
        new ESetValue(this);
        new ESetValueTC(this);
        new ReloadEnchants(this);
        new ReloadMaterials(this);
        new SaveEnchants(this);
        new SaveMaterials(this);
        new SetBal(this);
        new SetBalTC(this);
        new SetStock(this);
        new SetStockTC(this);
        new SetValue(this);
        new SetValueTC(this);

        // Enchant
        new EnchantHandBuy(this);
        new EnchantHandBuyTC(this);
        new EnchantHandSell(this);
        new EnchantHandSellTC(this);
        new EnchantHandValue(this);
        new EnchantHandValueTC(this);
        new EnchantInfo(this);
        new EnchantInfoTC(this);
        new EnchantValue(this);
        new EnchantValueTC(this);

        // Help
        new HelpCommand(this);
        new HelpCommandTC(this);

        // Mail
        new ClearMail(this);
        new ClearMailTC(this);
        new ReadMail(this);
        new ReadMailTC(this);

        // Market
        new Buy(this);
        new BuyTC(this);
        new HandBuy(this);
        new HandBuyTC(this);
        new HandInfo(this);
        new HandSell(this);
        new HandSellTC(this);
        new HandValue(this);
        new HandValueTC(this);
        new Info(this);
        new InfoTC(this);
        new Sell(this);
        new SellTC(this);
        new Value(this);
        new ValueTC(this);

        // Misc
        new Ping(this);

        // Money
        new Balance(this);
        new BalanceTC(this);
        new SendCash(this);
        new SendCashTC(this);

        // Placeholder API - handled differently to submodules
        // Automatically initiates - but must be last

        // If placeholder api found, register
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.expansionManager = new ExpansionManager(this);
            this.expansionManager.register();
            this.getConsole().info("Registered %s placeholders", this.expansionManager.getExpansionCount());
        } else {
            this.getConsole().warn("PlaceholderAPI was not found, disabling expansions.");
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
            if (!(value instanceof MemorySection)) this.getConsole().debug("   - %s: '%s'", setting.path, value);
        }
        this.console.debug("");
        this.console.debug("Markets:");
        this.console.debug("   - Materials: %s", this.materialManager.getMaterialCount());
        this.console.debug("      - Material Market Size: %s / %s", this.materialManager.getTotalMaterials(), this.materialManager.getDefaultTotalMaterials());
        this.console.debug("      - Material Market Inflation: %s%%", this.materialManager.getInflation());
        this.console.debug("   - Enchants: %s", this.enchantmentManager.getEnchantCount());
        this.console.debug("      - Enchant Market Size: %s / %s", this.enchantmentManager.getTotalEnchants(), this.enchantmentManager.getDefaultTotalEnchants());
        this.console.debug("      - Enchant Market Inflation: %s%%", this.enchantmentManager.getInflation());
        this.console.debug("");
    }

    /**
     * Returns the config manager
     */
    public ConfigManager getConfigManager() {
        return this.config;
    }

    /**
     * Returns the console
     */
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

    /**
     * Returns the help manager
     */
    public HelpManager getHelpManager() {return this.helpManager;}

}
