package org.divinitycraft.divinityeconomy.config;

import com.tchristofferson.configupdater.ConfigUpdater;
import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.DivinityModule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConfigManager extends DivinityModule {
    public static final String configFile = "config.yml";
    private String loadedVersion;

    public ConfigManager(DEPlugin main) {
        super(main);

        // Saves the .Jar config to the folder, if it doesn't exist.
        getMain().saveDefaultConfig();
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        // Get the config and plugin versions
        FileConfiguration config = getMain().getConfig();
        loadedVersion = config.getString(Setting.MAIN_VERSION_STRING.path);
        String pluginVersion = config.getDefaults().getString(Setting.MAIN_VERSION_STRING.path);

        this.getConsole().info("Detected config versions local/plugin | %s/%s", loadedVersion, pluginVersion);

        // Set last loaded version
        config.set(Setting.MAIN_VERSION_STRING.path, pluginVersion);
        this.saveFile(config, configFile);

        try {
            ConfigUpdater.update(getMain(), configFile, this.getFile(configFile), Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getMain().reloadConfig();
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
        return getMain().getConfig().get(setting.path, getMain().getConfig().getDefaults().get(setting.path));
    }

    /**
     * Returns an integer from the config
     */
    public Integer getInt(Setting setting) {
        return getMain().getConfig().getInt(setting.path, getMain().getConfig().getDefaults().getInt(setting.path));
    }

    /**
     * Returns a boolean from the config
     */
    public Boolean getBoolean(Setting setting) {
        return getMain().getConfig().getBoolean(setting.path, getMain().getConfig().getDefaults().getBoolean(setting.path));
    }

    /**
     * Returns a double from the config
     */
    public Double getDouble(Setting setting) {
        return getMain().getConfig().getDouble(setting.path, getMain().getConfig().getDefaults().getDouble(setting.path));
    }

    /**
     * Returns a string from the config
     */
    public String getString(Setting setting) {
        return getMain().getConfig().getString(setting.path, getMain().getConfig().getDefaults().getString(setting.path));
    }

    /**
     * Returns a string list from the config
     */
    public List<String> getStringList(Setting setting) {
        return getMain().getConfig().getStringList(setting.path);
    }

    /**
     * Sets a setting path to the given value
     */
    public void set(Setting setting, Object value) {
        getMain().getConfig().set(setting.path, value);
    }

    /**
     * Returns a file from the root folder
     */
    public File getFile(String file) {
        return new File(getMain().getDataFolder(), file);
    }

    /**
     * Returns a file from the given folder
     */
    public File getFile(File folder, String file) {
        return new File(folder, file);
    }

    public File getFolder(String folder) {
        File newFolder = new File(getMain().getDataFolder(), folder);
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
        return YamlConfiguration.loadConfiguration(new InputStreamReader(getMain().getResource(file)));
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

            // Save the file
            this.saveFile(config, file);
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
            file.save(new File(getMain().getDataFolder(), fileName));
        } catch (Exception e) {
            this.getConsole().severe("Failed to save file %s", fileName);
        }
    }


    /**
     * Returns the last loaded version
     */
    public String getLoadedVersion() {
        return this.loadedVersion;
    }
}
