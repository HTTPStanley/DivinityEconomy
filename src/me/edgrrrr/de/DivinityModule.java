package me.edgrrrr.de;

import me.edgrrrr.de.config.ConfigManager;
import me.edgrrrr.de.console.EconConsole;
import me.edgrrrr.de.economy.EconomyManager;
import me.edgrrrr.de.enchants.EnchantmentManager;
import me.edgrrrr.de.help.HelpManager;
import me.edgrrrr.de.mail.MailManager;
import me.edgrrrr.de.materials.MaterialManager;
import me.edgrrrr.de.player.PlayerManager;

import java.util.LinkedList;
import java.util.List;

public abstract class DivinityModule {
    private final DEPlugin main;
    private static List<DivinityModule> inits = new LinkedList<>();

    /**
     * Runs the initialisation of all modules
     */
    public static void runInit() {
        for (DivinityModule module : inits) {
            module.init();
        }
    }

    /**
     * Runs the de-initialisation of all modules from the rear to the front
     */
    public static void runDeinit() {
        for (int i = inits.size()-1; i > 0; i--) {
            inits.get(i).deinit();
        }
    }

    /**
     * Use init() to prevent NPE from getters.
     * @param main
     */
    public DivinityModule(DEPlugin main) {
        this.main = main;
        DivinityModule.inits.add(this);
    }

    public DivinityModule(DEPlugin main, boolean addInit) {
        this.main = main;
        if (addInit) DivinityModule.inits.add(this);
    }

    /**
     * Initialisation of the object
     */
    public abstract void init();

    /**
     * Shutdown of the object
     */
    public abstract void deinit();

    /**
     * Returns the help manager
     */
    public HelpManager getHelp() {
        return this.getMain().getHelpManager();
    }

    /**
     * Returns the material manager
     */
    public EnchantmentManager getEnchant() {
        return this.getMain().getEnchantmentManager();
    }

    /**
     * Returns the material manager
     */
    public MaterialManager getMaterial() {
        return this.getMain().getMaterialManager();
    }

    /**
     * Returns the config manager
     */
    public ConfigManager getConfig() {
        return this.getMain().getConfigManager();
    }

    /**
     * Returns the economy manager
     */
    public EconomyManager getEconomy() {
        return this.getMain().getEconomyManager();
    }

    /**
     * Returns the mail manager
     */
    public MailManager getMail() {
        return this.getMain().getMailManager();
    }

    /**
     * Returns the player manager
     */
    public PlayerManager getPlayer() {
        return this.getMain().getPlayerManager();
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
