package me.edgrrrr.de.lang;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

enum ProvidedLangFile {
    en_GB("en_GB.yml"), // English - UK
    fr_FR("fr_FR.yml"), // French - France
    de_DE("de_DE.yml"), // German - Germany
    es_ES("es_ES.yml"), // Spanish - Spain
    it_IT("it_IT.yml"), // Italian - Italy
    ru_RU("ru_RU.yml"), // Russian - Russia
    pl_PL("pl_PL.yml"), // Polish - Poland
    pt_PT("pt_PT.yml"), // Portuguese - Portugal
    da_DK("da_DK.yml"), // Danish - Denmark
    nl_NL("nl_NL.yml"), // Dutch - Netherlands
    sv_SE("sv_SE.yml"), // Swedish - Sweden
    tr_TR("tr_TR.yml"), // Turkish - Turkey
    zh_CN("zh_CN.yml"), // Chinese - China
    ja_JP("ja_JP.yml"), // Japanese - Japan

    ; // End of enum

    private final String path;

    ProvidedLangFile(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getResourcePath() {
        return path;
    }

    public String getFilePath() {
        return "locale/" + path;
    }
}

/**
 * Console class for sending uniform messages to players and the console.
 */
public class LangManager extends DivinityModule {
    private static final File langFolder = new File("locale");
    private static final String defaultLangStr = ProvidedLangFile.en_GB.getPath();
    private static File defaultLangFile;
    private static FileConfiguration defaultConfig;
    private static File langFile;
    private static FileConfiguration langConfig;
    private boolean translateItems;
    private String desiredLang;

    public LangManager(DEPlugin main) {
        super(main);
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        boolean isUpdated = false;
        boolean isCreated = false;

        // Get settings
        translateItems = getMain().getConfig().getBoolean(Setting.MAIN_TRANSLATE_ITEMS_BOOLEAN.path);
        desiredLang = getMain().getConfig().getString(Setting.MAIN_LANG_FILE_STRING.path);

        // Ensure lang folder exists
        this.getConfMan().getFolder(langFolder.getPath());

        // Loop through lang files in plugin
        for (ProvidedLangFile providedLangFile : ProvidedLangFile.values()) {
            try {
                // Get the file
                File thisLangFile = this.getConfMan().getFile(providedLangFile.getFilePath());
                FileConfiguration thisResourceFile = this.getConfMan().readResource(providedLangFile.getResourcePath());
                if (!thisLangFile.exists()) {
                    thisLangFile.createNewFile();
                    isCreated = true;
                }
                FileConfiguration thisLangConfig = this.getConfMan().readFile(thisLangFile);

                // Loop through resource file and add missing entries to lang file
                // Also update any entries that have a different number of '%s' in them
                for (String key : thisResourceFile.getKeys(true)) {
                    if (!thisLangConfig.contains(key)) {
                        thisLangConfig.set(key, thisResourceFile.get(key));
                        isUpdated = true;
                    } else {
                        String resourceString = thisResourceFile.getString(key);
                        String langString = thisLangConfig.getString(key);
                        if (resourceString != null && langString != null) {
                            int resourceCount = resourceString.length() - resourceString.replace("%s", "").length();
                            int langCount = langString.length() - langString.replace("%s", "").length();
                            if (resourceCount != langCount) {
                                thisLangConfig.set(key, resourceString);
                                isUpdated = true;
                            }
                        }
                    }
                }

                thisLangConfig.save(thisLangFile);
                if (isCreated) {
                    this.getConsole().info("Created lang file: %s.", providedLangFile.getPath());
                } else if (isUpdated) {
                    this.getConsole().info("Updated lang file: %s.", providedLangFile.getPath());
                }
            }
            catch (Exception e) {
                this.getConsole().severe("Couldn't update lang file: %s.", providedLangFile.getPath());
            }
        }


        // Loop through files and register them to the plugin
        for (File thisLangFile : this.getConfMan().getFolderFiles(langFolder.getPath())) {
            this.getConsole().debug("Found lang file: %s.", thisLangFile.getPath());
            if (thisLangFile.getName().equals(desiredLang)) {
                langFile = thisLangFile;
                langConfig = this.getConfMan().readFile(langFile);
            }
        }


        // Load default lang
        defaultLangFile = this.getConfMan().getFile(new File(langFolder, defaultLangStr).getPath());
        defaultConfig = this.getConfMan().readFile(defaultLangFile);


        // Check if lang file was loaded
        if (langFile == null || langConfig == null) {
            this.getConsole().severe("Couldn't load lang file: %s.", desiredLang);
            langFile = defaultLangFile;
            langConfig = defaultConfig;
        }


        // Check if default lang was loaded
        if (defaultConfig == null || !defaultLangFile.exists()) {
            this.getConsole().severe("Couldn't load default lang file: %s.", defaultLangStr);
        }


        this.getConsole().info("Loaded lang file: %s.", langFile.getName());
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {

    }

    /**
     * Returns a string from the config
     */
    public String get(LangEntry langEntry) {
        if (!defaultConfig.contains(langEntry.path)) {
            this.getConsole().warn("Lang entry not found in default lang file: %s.", langEntry.path);
        }

        if (langConfig == null) {
            return getDefault(langEntry);
        }

        if (!langConfig.contains(langEntry.path)) {
            this.getConsole().debug("Lang entry not found in selected lang file (%s): %s.", langFile.getName(), langEntry.path);
            return getDefault(langEntry);
        }

        return langConfig.getString(langEntry.path);
    }


    /**
     * Returns a string from the default config
     */
    public String getDefault(LangEntry langEntry) {
        return defaultConfig.getString(langEntry.path, String.format("MISSING LANG ENTRY (%s)", langEntry.path));
    }


    /**
     * Returns a formatted string from the config
     */
    public String get(LangEntry langEntry, Object... args) {
        return String.format(this.get(langEntry), args);
    }


    /**
     * Attempts to return a non-entry string from the config
     * A default must be provided in case the entry is not found
     */
    public String getItemName(String text, String defaultText) {
        if (langConfig == null || !translateItems) {
            return getDefaultItemName(text, defaultText);
        }

        return langConfig.getString(text, getDefaultItemName(text, defaultText));
    }


    /**
     * Attempts to return a non-entry string from the default config
     * A default must be provided in case the entry is not found
     */
    public String getDefaultItemName(String text, String defaultText) {
        return defaultConfig.getString(text, defaultText);
    }
}
