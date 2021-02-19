package EDGRRRR.DCE.Main;
/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConfigManager {
    private DCEPlugin app;

    // Prefixes for settings
    public String strMainPrefix = "main";
    public String strChatPrefix = "chat";
    public String strEconPrefix = "economy";
    public String strComPrefix = "commands";

    // Main settings
    public String strMainDebugMode = strMainPrefix + ".debugMode";
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


    public ConfigManager(DCEPlugin app){
        this.app = app;
        // Saves the .Jar config to the folder, if it doesn't exist.
        this.app.saveDefaultConfig();
        // Get the config and plugin versions
        String configVersion = this.app.getConfig().getString(strMainVersion);
        String pluginVersion = this.app.getDescription().getVersion();
        this.app.getLogger().info("Detected Config Version: " + configVersion + " & Plugin Version: " + pluginVersion);
        // Updates the config by copying defaults over
        // updates the version and saves.
        if (!(configVersion.matches(pluginVersion))) {
            this.app.getLogger().info("Updating config with defaults, your settings may need updating.");
            this.app.getConfig().options().copyDefaults(true);
            this.app.getConfig().set(strMainVersion, pluginVersion);
            this.app.saveConfig();
            // app.reloadConfig();
        }
    }
}
