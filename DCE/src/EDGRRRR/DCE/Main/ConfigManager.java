package EDGRRRR.DCE.Main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;

import java.io.File;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConfigManager {
    private final DCEPlugin app;

    // Prefixes for settings
    public String strMainPrefix = "main";
    public String strChatPrefix = "chat";
    public String strEconPrefix = "economy";
    public String strComPrefix = "commands";

    // Main settings
    public String strMainVersion = strMainPrefix + ".version";
    public String strMainSaveTimer = strMainPrefix + ".saveTimer";

    // Chat settings
    public String strChatDebug = strChatPrefix + ".chatDebug";
    public String strChatMsgPfx = strChatPrefix + ".prefix";
    public String strChatPfxSep = strChatPrefix + ".prefixSep";
    public String strChatPfxClr = strChatPrefix + ".prefixColour";
    public String strChatPfxSepClr = strChatPrefix + ".prefixSepColour";
    public String strChatConsPfx = strChatPrefix + ".prefixConsole";
    public String strChatInfClr = strChatPrefix + ".infoColour";
    public String strChatWrnClr = strChatPrefix + ".warnColour";
    public String strChatSvrClr = strChatPrefix + ".severeColour";
    public String strChatDbgClr = strChatPrefix + ".debugColour";

    // Economy settings
    public String strEconMinSendAmount = strEconPrefix + ".minSendAmount";
    public String strEconRoundingDigits = strEconPrefix + ".roundingDigits";
    public String strEconBaseQuantity = strEconPrefix + ".baseQuantity";
    public String strEconTaxScale = strEconPrefix + ".tax";
    public String strEconMinAccountBalance = strEconPrefix + ".minAccountBalance";

    // Commands settings
    public String strComBalance = strComPrefix + ".balance";
    public String strComClearBal = strComPrefix + ".clearBal";
    public String strComEditBal = strComPrefix + ".editBal";
    public String strComPing = strComPrefix + ".ping";
    public String strComSendCash = strComPrefix + ".sendCash";
    public String strComSetBal = strComPrefix + ".setBal";
    public String strComBuyItem = strComPrefix + ".buyItem";
    public String strComSellItem = strComPrefix + ".sellItem";
    public String strComHandSell = strComPrefix + ".handSell";
    public String strComHandBuy = strComPrefix + ".handBuy";
    public String strComValue = strComPrefix + ".value";
    public String strComSearch = strComPrefix + ".search";
    public String strComInfo = strComPrefix + ".info";
    public String strComHandInfo = strComPrefix + ".handInfo";
    public String strComHandValue = strComPrefix + ".handValue";


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
    public FileConfiguration readResource(String file) {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(this.app.getResource(file)));
    }

    /**
     * Reads and loads the config
     *
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
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
    public FileConfiguration loadConfig(String file) {
        // Instantiate default and user config
        FileConfiguration defConfig;
        FileConfiguration config = null;
        try {
            // Load default and user config
            defConfig = this.readResource(file);
            config = this.readFile(file);

            // If config is empty, overwrite with defaults
            // Empty can either mean non-existent or empty file.
            if (config.getValues(false).size() == 0) {
                config.setDefaults(defConfig);
                config.options().copyDefaults(true);
                config.save(new File(this.app.getDataFolder(), file));
            }
        } catch (Exception e) {
            config = null;
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
