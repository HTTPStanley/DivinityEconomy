package me.edgrrrr.de;

import me.edgrrrr.de.config.ConfigManager;
import me.edgrrrr.de.console.EconConsole;
import me.edgrrrr.de.economy.EconomyManager;
import me.edgrrrr.de.help.HelpManager;
import me.edgrrrr.de.mail.MailManager;
import me.edgrrrr.de.market.exp.ExpManager;
import me.edgrrrr.de.market.items.enchants.EnchantManager;
import me.edgrrrr.de.market.items.materials.MarketManager;
import me.edgrrrr.de.market.items.materials.block.BlockManager;
import me.edgrrrr.de.market.items.materials.entity.EntityManager;
import me.edgrrrr.de.market.items.materials.potion.PotionManager;
import me.edgrrrr.de.player.PlayerManager;

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
        // Make sure to deinitialise before initialising.
        if (initialisedModules.size() > 0) DivinityModule.runDeinit();

        for (DivinityModule module : DivinityModule.modules) {
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
        return this.getMain().getHelpMan();
    }

    /**
     * Returns the material manager
     */
    public EnchantManager getEnchMan() {
        return this.getMain().getEnchMan();
    }

    /**
     * Returns the material manager
     */
    public BlockManager getMatMan() {
        return this.getMain().getMatMan();
    }

    /**
     * Returns the potion manager
     */
    public PotionManager getPotMan() {
        return this.getMain().getPotMan();
    }

    /**
     * Returns the entity manager
     */
    public EntityManager getEntMan() {
        return this.getMain().getEntMan();
    }

    /**
     * Returns the experience manager
     */
    public ExpManager getExpMan() {
        return this.getMain().getExpMan();
    }


    /**
     * Returns the market manager
     */
    public MarketManager getMarkMan() {
        return this.getMain().getMarkMan();
    }

    /**
     * Returns the config manager
     */
    public ConfigManager getConfMan() {
        return this.getMain().getConfMan();
    }

    /**
     * Returns the economy manager
     */
    public EconomyManager getEconMan() {
        return this.getMain().getEconMan();
    }

    /**
     * Returns the mail manager
     */
    public MailManager getMailMan() {
        return this.getMain().getMailMan();
    }

    /**
     * Returns the player manager
     */
    public PlayerManager getPlayMan() {
        return this.getMain().getPlayMan();
    }

    /**
     * Returns the console manager
     */
    public EconConsole getConsole() {
        return this.getMain().getConsole();
    }

    /**
     * Returns the main DEPlugin (JavaPlugin) object
     */
    public DEPlugin getMain() {
        return this.main;
    }
}
