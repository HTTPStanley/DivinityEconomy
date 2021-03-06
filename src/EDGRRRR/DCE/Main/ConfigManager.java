package EDGRRRR.DCE.Main;

import com.sun.istack.internal.NotNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

import java.io.File;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConfigManager {
    private final DCEPlugin app;

    // Prefixes for settings
    public final String strMainPrefix = "main";
    public final String strChatPrefix = "chat";
    public final String strEconomyPrefix = "economy";
    public final String strCommandsPrefix = "commands";
    // Sub parts of commands
    public final String strComAdminPrefix = strCommandsPrefix + ".admin";
    public final String strComMailPrefix = strCommandsPrefix + ".mail";
    public final String strComEconPrefix = strCommandsPrefix + ".economy";
    public final String strComMiscPrefix = strCommandsPrefix + ".misc";
    public final String strComMarketPrefix = strCommandsPrefix + ".market";
    public final String strComEnchantPrefix = strCommandsPrefix + ".enchant";
    // --
    public final String strMarketPrefix = "market";
    // Sub parts of market
    public final String strMaterialsPrefix = strMarketPrefix + ".materials";
    public final String strEnchantsPrefix = strMarketPrefix + ".enchants";
    // --

    // Main settings
    public final String strMainVersion = strMainPrefix + ".version";

    // Chat settings
    public final String strChatDebug = strChatPrefix + ".chatDebug";
    public final String strChatMsgPfx = strChatPrefix + ".prefix";
    public final String strChatPfxSep = strChatPrefix + ".prefixSep";
    public final String strChatPfxClr = strChatPrefix + ".prefixColour";
    public final String strChatPfxSepClr = strChatPrefix + ".prefixSepColour";
    public final String strChatConsPfx = strChatPrefix + ".prefixConsole";
    public final String strChatInfClr = strChatPrefix + ".infoColour";
    public final String strChatWrnClr = strChatPrefix + ".warnColour";
    public final String strChatSvrClr = strChatPrefix + ".severeColour";
    public final String strChatDbgClr = strChatPrefix + ".debugColour";

    // Economy settings
    public final String strEconomyMinSendAmount = strEconomyPrefix + ".minSendAmount";
    public final String strEconomyRoundingDigits = strEconomyPrefix + ".roundingDigits";
    public final String strEconomyMinAccountBalance = strEconomyPrefix + ".minAccountBalance";

    // Market settings
    public final String strMarketSaveTimer = strMarketPrefix + ".saveTimer";
    // (Market)Material settings
    public final String strMaterialEnable = strMaterialsPrefix + ".enable";
    public final String strMaterialBaseQuantity = strMaterialsPrefix + ".baseQuantity";
    public final String strMaterialBuyTax = strMaterialsPrefix + ".buyTax";
    public final String strMaterialSellTax = strMaterialsPrefix + ".sellTax";
    // (Market)Enchant settings
    public final String strEnchantEnable = strEnchantsPrefix + ".enable";
    public final String strEnchantBaseQuantity = strEnchantsPrefix + ".baseQuantity";
    public final String strEnchantBuyTax = strEnchantsPrefix + ".buyTax";
    public final String strEnchantSellTax = strEnchantsPrefix + ".sellTax";

    // Commands settings
    public final String strComBalance = strComEconPrefix + ".balance";
    public final String strComClearBal = strComAdminPrefix + ".clearBal";
    public final String strComEditBal = strComAdminPrefix + ".editBal";
    public final String strComPing = strComMiscPrefix + ".ping";
    public final String strComSendCash = strComEconPrefix + ".sendCash";
    public final String strComSetBal = strComAdminPrefix + ".setBal";
    public final String strComBuyItem = strComMarketPrefix + ".buyItem";
    public final String strComSellItem = strComMarketPrefix + ".sellItem";
    public final String strComHandSell = strComMarketPrefix + ".handSell";
    public final String strComHandBuy = strComMarketPrefix + ".handBuy";
    public final String strComValue = strComMarketPrefix + ".value";
    public final String strComSearch = strComMarketPrefix + ".search";
    public final String strComInfo = strComMarketPrefix + ".info";
    public final String strComHandInfo = strComMarketPrefix + ".handInfo";
    public final String strComHandValue = strComMarketPrefix + ".handValue";
    public final String strComReadMail = strComMailPrefix + ".readMail";
    public final String strComClearMail = strComMailPrefix + ".clearMail";
    public final String strComEnchantHandSell = strComEnchantPrefix + ".eHandSell";


    public ConfigManager(DCEPlugin app) {
        this.app = app;
        // Saves the .Jar config to the folder, if it doesn't exist.
        this.app.saveDefaultConfig();
        // Get the config and plugin versions
        String configVersion = this.app.getConfig().getString(strMainVersion);
        String pluginVersion = this.app.getDescription().getVersion();
        this.app.getLogger().info("Detected Config Version: " + configVersion + " & Plugin Version: " + pluginVersion);
        // Updates the config by copying defaults over
        // updates the version and saves.
        if (!(configVersion.equals(pluginVersion))) {
            this.app.getLogger().info("Updating config with defaults, your settings may need updating.");
            this.app.getConfig().options().copyDefaults(true);
            this.app.getConfig().set(strMainVersion, pluginVersion);
            this.app.saveConfig();
            // app.reloadConfig();
        }
    }

    /**
     * Reads and loads the default config
     *
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    @NotNull
    public FileConfiguration readResource(String file) {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(this.app.getResource(file)));
    }

    /**
     * Reads and loads the config
     *
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    @NotNull
    public FileConfiguration readFile(String file) {
        return YamlConfiguration.loadConfiguration(new File(this.app.getDataFolder(), file));
    }

    /**
     * Loads the default and current config files If the config file is empty or
     * non-existent, it will be overwritten with the default config And returned
     *
     * @param file - The file to load
     * @return FileConfiguration - The file config
     */
    @NotNull
    public FileConfiguration loadConfig(String file) {
        // Instantiate default and user config
        FileConfiguration defConfig;
        FileConfiguration config = null;
        // Load default and user config
        defConfig = this.readResource(file);
        config = this.readFile(file);

        // If config is empty, overwrite with defaults
        // Empty can either mean non-existent or empty file.
        if (config.getValues(false).size() == 0) {
            config.setDefaults(defConfig);
            config.options().copyDefaults(true);

            try {
                config.save(new File(this.app.getDataFolder(), file));
            } catch (Exception e) {
                this.app.getConsoleManager().severe(String.format("Couldn't save config with new values: %s", file));
            }
        }

        return config;
    }

    /**
     * Saves a file to a file path
     *
     * @param file     - The file config to save
     * @param fileName - The file name to save to
     */
    public void saveFile(FileConfiguration file, String fileName) {
        try {
            file.save(new File(this.app.getDataFolder(), fileName));
        } catch (Exception e) {
            this.app.getConsoleManager().severe("Couldn't handle " + fileName + " :" + e.getMessage());
        }
    }
}
