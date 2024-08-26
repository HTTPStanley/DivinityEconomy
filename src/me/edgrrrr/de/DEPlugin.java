package me.edgrrrr.de;

import me.edgrrrr.de.commands.admin.*;
import me.edgrrrr.de.commands.enchants.*;
import me.edgrrrr.de.commands.experience.ExperienceBuy;
import me.edgrrrr.de.commands.experience.ExperienceBuyTC;
import me.edgrrrr.de.commands.experience.ExperienceSell;
import me.edgrrrr.de.commands.experience.ExperienceSellTC;
import me.edgrrrr.de.commands.help.HelpCommand;
import me.edgrrrr.de.commands.help.HelpCommandTC;
import me.edgrrrr.de.commands.mail.ClearMail;
import me.edgrrrr.de.commands.mail.ClearMailTC;
import me.edgrrrr.de.commands.mail.ReadMail;
import me.edgrrrr.de.commands.mail.ReadMailTC;
import me.edgrrrr.de.commands.market.*;
import me.edgrrrr.de.commands.misc.EconomyNotifications;
import me.edgrrrr.de.commands.misc.EconomyNotificationsTC;
import me.edgrrrr.de.commands.misc.Ping;
import me.edgrrrr.de.commands.money.*;
import me.edgrrrr.de.config.ConfigManager;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.console.Console;
import me.edgrrrr.de.console.LogLevel;
import me.edgrrrr.de.economy.EconomyManager;
import me.edgrrrr.de.help.HelpManager;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.lang.LangManager;
import me.edgrrrr.de.mail.MailManager;
import me.edgrrrr.de.market.exp.ExpManager;
import me.edgrrrr.de.market.items.enchants.EnchantManager;
import me.edgrrrr.de.market.items.materials.MarketManager;
import me.edgrrrr.de.market.items.materials.block.BlockManager;
import me.edgrrrr.de.market.items.materials.entity.EntityManager;
import me.edgrrrr.de.market.items.materials.potion.PotionManager;
import me.edgrrrr.de.migrators.MigrationManager;
import me.edgrrrr.de.placeholders.ExpansionManager;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.world.WorldManager;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

/**
 * The Main Class of the plugin
 * Hooks everything together
 */
public class DEPlugin extends JavaPlugin {
    // bStats ID
    private final int bStatsID = 22013; // ID for bStats
    private final String defaultLocaleString = "en_GB";

    // The config
    private ConfigManager config;
    // The console
    private Console console;
    // The language manager
    private LangManager lang;
    // The world manager
    private WorldManager worldManager;
    // The economy
    private EconomyManager economyManager;
    // The mail manager
    private MailManager mailManager;
    // The player manager
    private PlayerManager playerManager;
    // The help manager
    private HelpManager helpManager;
    // The market manager
    private MarketManager marketManager;
    // The market manager
    private BlockManager blockManager;
    // The market manager
    private EntityManager entityManager;
    // The market manager
    private PotionManager potionManager;
    // The enchant manager
    private EnchantManager enchantManager;
    // Experience manager
    private ExpManager expManager;
    // The placeholder api expansion manager
    private ExpansionManager expansionManager;
    // The migration manager
    private MigrationManager migrationManager;


    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        // Set the default locale
        Locale.setDefault(Locale.of(defaultLocaleString));

        // Instantiates all modules
        LogLevel.loadValuesFromConfig((YamlConfiguration) this.getConfig());
        this.config = new ConfigManager(this);
        this.console = new Console(this);
        this.lang = new LangManager(this);
        this.migrationManager = new MigrationManager(this);
        this.worldManager = new WorldManager(this);
        this.playerManager = new PlayerManager(this);
        this.economyManager = new EconomyManager(this);
        this.marketManager = new MarketManager(this);
        this.blockManager = new BlockManager(this);
        this.potionManager = new PotionManager(this);
        this.entityManager = new EntityManager(this);
        this.enchantManager = new EnchantManager(this);
        this.expManager = new ExpManager(this);
        this.mailManager = new MailManager(this);
        this.helpManager = new HelpManager(this);

        // Initialise config
        this.config.init();
        DivinityModule.addModule(this.config, true);

        // Initialise language
        try {
            Locale configLocale = Locale.of(this.getConfMan().getString(Setting.MAIN_SYSTEM_LOCALE_STRING));
            Locale.setDefault(configLocale);
        } catch (Exception e) {
            this.console.warn(LangEntry.GENERIC_LocaleError.get(this), this.getConfMan().getString(Setting.MAIN_SYSTEM_LOCALE_STRING));
            e.printStackTrace();
        }

        // Initialise language
        this.lang.init();
        DivinityModule.addModule(this.lang, true);

        // Run migrations
        this.migrationManager.init();
        DivinityModule.addModule(this.migrationManager, true);

        // Initialise world manager
        this.worldManager.init();
        DivinityModule.addModule(this.worldManager, true);

        // Initialise player manager
        this.playerManager.init();
        DivinityModule.addModule(this.playerManager, true);

        // Initialise economy
        this.economyManager.init();
        // Check that the economy is enabled
        if (this.economyManager.getVaultEconomy() == null) {
            this.console.severe(LangEntry.GENERIC_EconomyNotEnabled.get(this));
            this.shutdown();
            return;
        }
        this.console.info(LangEntry.GENERIC_EconomyEnabled.get(this), this.economyManager.getVaultEconomy());
        DivinityModule.addModule(this.economyManager, true);

        // Initialisation of all modules
        DivinityModule.runInit();

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
        new Reload(this);
        new ReloadTC(this);
        new Save(this);
        new SaveTC(this);
        new SetBal(this);
        new SetBalTC(this);
        new SetStock(this);
        new SetStockTC(this);
        new SetValue(this);
        new SetValueTC(this);
        new Reload(this);
        new BanItem(this);
        new BanItemTC(this);
        new BanEnchant(this);
        new BanEnchantTC(this);

        // Experience
        new ExperienceBuy(this);
        new ExperienceBuyTC(this);
        new ExperienceSell(this);
        new ExperienceSellTC(this);

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
        new EnchantSellAll(this);
        new EnchantSellAllTC(this);

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
        new SellAll(this);
        new SellAllTC(this);
        new Value(this);
        new ValueTC(this);
        new ItemList(this);
        new ItemListTC(this);

        // Misc
        new Ping(this);
        new EconomyNotifications(this);
        new EconomyNotificationsTC(this);

        // Money
        new Balance(this);
        new BalanceTC(this);
        new SendCash(this);
        new SendCashTC(this);
        new ListBalances(this);

        // Placeholder API - handled differently to submodules
        // Automatically initiates - but must be last
        // If placeholder api found, register
        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (this.getConfMan().getBoolean(Setting.MAIN_ENABLE_PAPI_BOOLEAN)) {
                this.expansionManager = new ExpansionManager(this);
                this.expansionManager.register();
                this.getConsole().info(LangEntry.GENERIC_PAPIEnabled.get(this), this.expansionManager.getExpansionCount());
            } else {
                this.getConsole().warn(LangEntry.GENERIC_PAPINotEnabled.get(this));
            }
        } else {
            this.getConsole().warn(LangEntry.GENERIC_PAPINotFound.get(this));
        }

        // Enable bStats
        if (this.getConfMan().getBoolean(Setting.MAIN_ENABLE_BSTATS_BOOLEAN)) {
            new Metrics(this, bStatsID);
        }

        // Done :)
        this.describe();
        this.console.severe(LangEntry.GENERIC_PluginEnabled.get(this));
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        DivinityModule.runDeinit();
        this.console.severe(LangEntry.GENERIC_PluginDisabled.get(this));
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
        this.console.debug(LangEntry.DESCRIBE_Header.get(this));
        this.console.debug(LangEntry.ECONOMY_Prefix.get(this), this.console.getCurrencyPrefix());
        this.console.debug(LangEntry.ECONOMY_Suffix.get(this), this.console.getCurrencySuffix());
        this.console.debug(LangEntry.DESCRIBE_Settings.get(this));
        for (Setting setting : Setting.values()) {
            Object value = this.getConfMan().get(setting);
            if (!(value instanceof MemorySection)) this.getConsole().debug("   - %s: '%s'", setting.path, value);
        }
        this.console.debug("");
        this.console.debug(LangEntry.DESCRIBE_Markets.get(this));
        this.console.debug(LangEntry.DESCRIBE_Materials.get(this, this.blockManager.getItemCount()));
        this.console.debug(LangEntry.DESCRIBE_MaterialMarketSize.get(this, this.blockManager.getTotalItems(), this.blockManager.getDefaultTotalItems()));
        this.console.debug(LangEntry.DESCRIBE_MaterialMarketInflation.get(this, this.blockManager.getInflation()));
        this.console.debug(LangEntry.DESCRIBE_Enchants.get(this, this.enchantManager.getEnchantCount()));
        this.console.debug(LangEntry.DESCRIBE_EnchantMarketSize.get(this, this.enchantManager.getTotalItems(), this.enchantManager.getDefaultTotalItems()));
        this.console.debug(LangEntry.DESCRIBE_EnchantMarketInflation.get(this, this.enchantManager.getInflation()));
        this.console.debug(LangEntry.DESCRIBE_Experience.get(this, this.expManager.getItemCount()));
        this.console.debug(LangEntry.DESCRIBE_ExperienceMarketSize.get(this, this.expManager.getTotalItems(), this.expManager.getDefaultTotalItems()));
        this.console.debug(LangEntry.DESCRIBE_ExperienceMarketInflation.get(this, this.expManager.getInflation()));
        this.console.debug(LangEntry.DESCRIBE_PotionsMarketInflation.get(this, this.potionManager.getItemCount()));
        this.console.debug(LangEntry.DESCRIBE_PotionsMarketInflation.get(this, this.potionManager.getTotalItems(), this.potionManager.getDefaultTotalItems()));
        this.console.debug(LangEntry.DESCRIBE_PotionsMarketInflation.get(this, this.potionManager.getInflation()));
        this.console.debug(LangEntry.DESCRIBE_EntitiesMarketInflation.get(this, this.entityManager.getItemCount()));
        this.console.debug(LangEntry.DESCRIBE_EntitiesMarketInflation.get(this, this.entityManager.getTotalItems(), this.entityManager.getDefaultTotalItems()));
        this.console.debug(LangEntry.DESCRIBE_EntitiesMarketInflation.get(this, this.entityManager.getInflation()));
        this.console.debug("");
    }

    /**
     * Returns the config manager
     */
    public ConfigManager getConfMan() {
        return this.config;
    }

    /**
     * Returns the console
     */
    public Console getConsole() {
        return this.console;
    }

    /**
     * Returns the language manager
     */
    public LangManager getLang() {
        return this.lang;
    }

    /**
     * Returns the world manager
     */
    public WorldManager getWorldMan() {
        return this.worldManager;
    }

    /**
     * Returns the economy manager
     * Handles all Vault API actions. Such as sending, adding, removing and setting cash.
     *
     * @return EconomyManager
     */
    public EconomyManager getEconMan() {
        return this.economyManager;
    }

    /**
     * Returns the market manager
     * Manages all material items that can exist in ones inventory
     *
     * @return MarketManager
     */
    public MarketManager getMarkMan() {
        return this.marketManager;
    }

    /**
     * Returns the Material Manager
     * This is used for managing materials and their value.
     *
     * @return MaterialManager
     */
    public BlockManager getMatMan() {
        return this.blockManager;
    }

    /**
     * Returns the Potion Manager
     * This is used for managing potions and their value.
     *
     * @return PotionManager
     */
    public PotionManager getPotMan() {
        return this.potionManager;
    }

    /**
     * Returns the Entity Manager
     * This is used for managing entities and their value.
     *
     * @return EntityManager
     */
    public EntityManager getEntMan() {
        return this.entityManager;
    }

    /**
     * Returns the Experience Manager
     * This is used for managing experience and its value.
     *
     * @return ExpManager
     */
    public ExpManager getExpMan() {
    	return this.expManager;
    }

    /**
     * Returns the mail manager
     * Used to getting, creating and setting Mail for, mostly offline, users.
     *
     * @return MailManager
     */
    public MailManager getMailMan() {
        return this.mailManager;
    }

    /**
     * Returns the player manager
     * This is currently used for getting Player and OfflinePlayer objects
     *
     * @return PlayerManager
     */
    public PlayerManager getPlayMan() {
        return this.playerManager;
    }

    /**
     * Returns the enchantment manager
     * This is used for handling enchantments on items and determining their value.
     *
     * @return EnchantmentManager
     */
    public EnchantManager getEnchMan() {
        return this.enchantManager;
    }

    /**
     * Returns the help manager
     */
    public HelpManager getHelpMan() {
        return this.helpManager;
    }

    /**
     * Returns the expansion manager
     */
    public ExpansionManager getExpansionManager() {
        return this.expansionManager;
    }
}
