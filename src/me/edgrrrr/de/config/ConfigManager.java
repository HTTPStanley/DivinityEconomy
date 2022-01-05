package me.edgrrrr.de.config;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConfigManager extends DivinityModule {

    public ConfigManager(DEPlugin main) {
        super(main);

        // Saves the .Jar config to the folder, if it doesn't exist.
        this.getMain().saveDefaultConfig();
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        // Get the config and plugin versions
        String configVersion = this.getMain().getConfig().getString(Setting.MAIN_VERSION_STRING.path);
        String pluginVersion = this.getMain().getConfig().getDefaults().getString(Setting.MAIN_VERSION_STRING.path);

        this.getConsole().info("Detected config versions local/plugin | %s/%s", configVersion, pluginVersion);
        // Updates the config by copying defaults over
        // updates the version and saves.
        if (!(configVersion.equals(pluginVersion))) {
            this.getConsole().info("Updating config with new defaults, your settings may need updating.");
        }
        this.getMain().getConfig().options().copyDefaults(true);
        this.getMain().getConfig().addDefaults(this.getMain().getConfig().getDefaults());
        this.getMain().getConfig().set(Setting.MAIN_VERSION_STRING.path, pluginVersion);
        this.getMain().saveConfig();
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {

    }

    /**
     * Returns a generic object from the config
     */
    public Object get(Setting setting) {
        return this.getMain().getConfig().get(setting.path, this.getMain().getConfig().getDefaults().get(setting.path));
    }

    /**
     * Returns an integer from the config
     */
    public Integer getInt(Setting setting) {
        return this.getMain().getConfig().getInt(setting.path, this.getMain().getConfig().getDefaults().getInt(setting.path));
    }

    /**
     * Returns a boolean from the config
     */
    public Boolean getBoolean(Setting setting) {
        return this.getMain().getConfig().getBoolean(setting.path, this.getMain().getConfig().getDefaults().getBoolean(setting.path));
    }

    /**
     * Returns a double from the config
     */
    public Double getDouble(Setting setting) {
        return this.getMain().getConfig().getDouble(setting.path, this.getMain().getConfig().getDefaults().getDouble(setting.path));
    }

    /**
     * Returns a string from the config
     */
    public String getString(Setting setting) {
        return this.getMain().getConfig().getString(setting.path, this.getMain().getConfig().getDefaults().getString(setting.path));
    }

    /**
     * Returns a string list from the config
     */
    public List<String> getStringList(Setting setting) {
        return this.getMain().getConfig().getStringList(setting.path);
    }

    /**
     * Sets a setting path to the given value
     */
    public void set(Setting setting, Object value) {
        this.getMain().getConfig().set(setting.path, value);
    }

    /**
     * Returns a file from the root folder
     */
    public File getFile(String file) {
        return new File(this.getMain().getDataFolder(), file);
    }

    /**
     * Returns a file from the given folder
     */
    public File getFile(File folder, String file) {
        return new File(folder, file);
    }

    public File getFolder(String folder) {
        File newFolder = new File(this.getMain().getDataFolder(), folder);
        if (!newFolder.exists()) {
            if (newFolder.mkdir()) return newFolder;
            else return null;
        } else {
            if (newFolder.isDirectory()) return newFolder;
            else return null;
        }
    }

    public List<File> getFolderFiles(String folder) {
        File newFolder = this.getFolder(folder);
        if (newFolder != null) {
            return Arrays.asList(this.getFolder(folder).listFiles());
        } else return Collections.emptyList();
    }


    /**
     * Reads and loads the default config
     *
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    public FileConfiguration readResource(String file) {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(this.getMain().getResource(file)));
    }

    /**
     * Reads and loads a config
     *
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    public FileConfiguration readFile(String file) {
        return this.readFile(this.getFile(file));
    }

    /**
     * Reads and loads a config
     *
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    public FileConfiguration readFile(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Returns a file with updated values from the default resource if they are missing.
     *
     * @param file
     */
    public FileConfiguration loadFile(String file) {
        // Instantiate default and user config
        FileConfiguration defConfig;
        FileConfiguration config;
        // Load default and user config
        defConfig = this.readResource(file);
        config = this.readFile(file);

        // If config is empty, overwrite with defaults
        // Empty can either mean non-existent or empty file.
        if (config.getValues(false).size() == 0) {
            config.setDefaults(defConfig);
            config.options().copyDefaults(true);

            try {
                config.save(new File(this.getMain().getDataFolder(), file));
            } catch (Exception e) {
                this.getConsole().severe("Couldn't save config with new values: %s", file);
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
            file.save(new File(this.getMain().getDataFolder(), fileName));
        } catch (Exception e) {
            this.getConsole().severe("Couldn't handle %s: %s", fileName, e.getMessage());
        }
    }
}
