package edgrrrr.dce.config;

import com.sun.istack.internal.NotNull;
import edgrrrr.dce.main.DCEPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;

import java.io.File;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConfigManager {
    private final DCEPlugin app;

    public ConfigManager(DCEPlugin app) {
        this.app = app;
        // Saves the .Jar config to the folder, if it doesn't exist.
        this.app.saveDefaultConfig();
        // Get the config and plugin versions
        String configVersion = this.app.getConfig().getString(Setting.MAIN_VERSION_STRING.path());
        String pluginVersion = this.app.getDescription().getVersion();
        this.app.getLogger().info("Detected Config Version: " + configVersion + " & Plugin Version: " + pluginVersion);
        // Updates the config by copying defaults over
        // updates the version and saves.
        if (!(configVersion.equals(pluginVersion))) {
            this.app.getLogger().info("Updating config with defaults, your settings may need updating.");
            this.app.getConfig().options().copyDefaults(true);
            this.app.getConfig().set(Setting.MAIN_VERSION_STRING.path(), pluginVersion);
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
