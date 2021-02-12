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

    // Commands settings
    public String strComBalance = strComPrefix + ".balance";
    public String strComClearBal = strComPrefix + ".clearBal";
    public String strComEditBal = strComPrefix + ".editBal";
    public String strComPing = strComPrefix + ".ping";
    public String strComSendCash = strComPrefix + ".sendCash";
    public String strComSetBal = strComPrefix + ".setBal";
    public String strComBuyItem = strComPrefix + ".buyItem";
    public String strComValue = strComPrefix + ".value";
    public String strComSearch = strComPrefix + ".search";
    public String strComInfo = strComPrefix + ".info";


    public ConfigManager(DCEPlugin app){
        this.app = app;
        // Saves the .Jar config to the folder, if it doesn't exist.
        app.saveDefaultConfig();
        // Get the config and plugin versions
        String configVersion = app.getConfig().getString(strMainVersion);
        String pluginVersion = app.getDescription().getVersion();
        app.getLogger().info("Detected Config Version: " + configVersion + " & Plugin Version: " + pluginVersion);
        // Updates the config by copying defaults over
        // updates the version and saves.
        if (!(configVersion.matches(pluginVersion))) {
            app.getLogger().info("Updating config with defaults, your settings may need updating.");
            app.getConfig().options().copyDefaults(true);
            app.getConfig().set(strMainVersion, pluginVersion);
            app.saveConfig();
            // app.reloadConfig();
        }
    }
}
