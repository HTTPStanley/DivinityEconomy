package me.edgrrrr.de;

import me.edgrrrr.de.config.ConfigManager;
import me.edgrrrr.de.console.EconConsole;
import me.edgrrrr.de.economy.EconomyManager;
import me.edgrrrr.de.help.HelpManager;
import me.edgrrrr.de.lang.LangManager;
import me.edgrrrr.de.mail.MailManager;
import me.edgrrrr.de.market.exp.ExpManager;
import me.edgrrrr.de.market.items.enchants.EnchantManager;
import me.edgrrrr.de.market.items.materials.MarketManager;
import me.edgrrrr.de.market.items.materials.block.BlockManager;
import me.edgrrrr.de.market.items.materials.entity.EntityManager;
import me.edgrrrr.de.market.items.materials.potion.PotionManager;
import me.edgrrrr.de.player.PlayerManager;
import me.edgrrrr.de.world.WorldManager;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;

public abstract class DivinityModule {
    private static final Logger logger = Logger.getLogger("Minecraft");
    private static final Queue<DivinityModule> modules = new ArrayDeque<>();
    private static final Queue<DivinityModule> initialisedModules = new ArrayDeque<>();
    private final DEPlugin main;

    /**
     * Use init() to prevent NPE from getters.
     *
     * @param main
     */
    public DivinityModule(DEPlugin main) {
        this.main = main;
        DivinityModule.modules.add(this);
    }

    public DivinityModule(DEPlugin main, boolean addInit) {
        this.main = main;
        if (addInit) DivinityModule.modules.add(this);
    }

    /**
     * Runs the initialisation of all modules
     */
    public static void runInit() {
        for (DivinityModule module : DivinityModule.modules) {
            if (initialisedModules.contains(module)) continue;
            try {
                module.init();
                initialisedModules.add(module);
            } catch (Exception e) {
                logger.severe(String.format("Module '%s' failed to initialise: %s", module.getClass().getName(), e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs the de-initialisation of all modules from the rear to the front
     */
    public static void runDeinit() {
        while (!DivinityModule.initialisedModules.isEmpty()) {
            DivinityModule module = initialisedModules.remove();
            try {
                module.deinit();
            } catch (Exception e) {
                logger.severe(String.format("Module '%s' failed to deinitialise: %s", module.getClass().getName(), e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the main plugin
     */
    public static void addModule(DivinityModule module, boolean isInitiated) {
        // Ensure that the module is not null
        if (module == null) return;

        // Ensure that the module is not already added
        if (!DivinityModule.modules.contains(module)) DivinityModule.modules.add(module);

        // Ensure that the module is not already initialised
        if (isInitiated && !(DivinityModule.initialisedModules.contains(module))) DivinityModule.initialisedModules.add(module);
    }

    /**
     * Initialisation of the object
     */
    protected abstract void init();

    /**
     * Shutdown of the object
     */
    protected abstract void deinit();

    /**
     * Returns the help manager
     */
    public HelpManager getHelpMan() {
        return getMain().getHelpMan();
    }

    /**
     * Returns the material manager
     */
    public EnchantManager getEnchMan() {
        return getMain().getEnchMan();
    }

    /**
     * Returns the material manager
     */
    public BlockManager getMatMan() {
        return getMain().getMatMan();
    }

    /**
     * Returns the potion manager
     */
    public PotionManager getPotMan() {
        return getMain().getPotMan();
    }

    /**
     * Returns the entity manager
     */
    public EntityManager getEntMan() {
        return getMain().getEntMan();
    }

    /**
     * Returns the experience manager
     */
    public ExpManager getExpMan() {
        return getMain().getExpMan();
    }


    /**
     * Returns the world manager
     */
    public WorldManager getWorldMan() {
        return getMain().getWorldMan();
    }


    /**
     * Returns the market manager
     */
    public MarketManager getMarkMan() {
        return getMain().getMarkMan();
    }

    /**
     * Returns the config manager
     */
    public ConfigManager getConfMan() {
        return getMain().getConfMan();
    }

    /**
     * Returns the economy manager
     */
    public EconomyManager getEconMan() {
        return getMain().getEconMan();
    }

    /**
     * Returns the mail manager
     */
    public MailManager getMailMan() {
        return getMain().getMailMan();
    }

    /**
     * Returns the player manager
     */
    public PlayerManager getPlayMan() {
        return getMain().getPlayMan();
    }

    /**
     * Returns the lang manager
     */
    public LangManager getLang() {
        return getMain().getLang();
    }

    /**
     * Returns the console manager
     */
    public EconConsole getConsole() {
        return getMain().getConsole();
    }

    /**
     * Returns the main DEPlugin (JavaPlugin) object
     */
    public DEPlugin getMain() {
        return this.main;
    }
}
