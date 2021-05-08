package edgrrrr.configapi;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConfigManager implements ConfigManagerAPI {
    private final JavaPlugin app;
    private final Logger logger;

    public ConfigManager(JavaPlugin app) {
        this.app = app;
        this.logger = app.getLogger();
        // Saves the .Jar config to the folder, if it doesn't exist.
        this.app.saveDefaultConfig();
        // Get the config and plugin versions
        String configVersion = this.app.getConfig().getString(Setting.MAIN_VERSION_STRING.path);
        String pluginVersion = this.app.getConfig().getDefaults().getString(Setting.MAIN_VERSION_STRING.path);
        this.logger.info("Detected Config Version: " + configVersion + " & Plugin Version: " + pluginVersion);
        // Updates the config by copying defaults over
        // updates the version and saves.
        if (!(configVersion.equals(pluginVersion))) {
            this.logger.info("Updating config with defaults, your settings may need updating.");
            this.app.getConfig().options().copyDefaults(true);
            this.app.getConfig().set(Setting.MAIN_VERSION_STRING.path, pluginVersion);
            this.app.saveConfig();
        }
    }

    @Override
    public Object get(Setting setting) {
        return this.app.getConfig().get(setting.path, this.app.getConfig().getDefaults().get(setting.path));
    }

    @Override
    public Integer getInt(Setting setting) {
        return this.app.getConfig().getInt(setting.path, this.app.getConfig().getDefaults().getInt(setting.path));
    }

    @Override
    public Boolean getBoolean(Setting setting) {
        return this.app.getConfig().getBoolean(setting.path, this.app.getConfig().getDefaults().getBoolean(setting.path));
    }

    @Override
    public Double getDouble(Setting setting) {
        return this.app.getConfig().getDouble(setting.path, this.app.getConfig().getDefaults().getDouble(setting.path));
    }

    @Override
    public String getString(Setting setting) {
        return this.app.getConfig().getString(setting.path, this.app.getConfig().getDefaults().getString(setting.path));
    }

    @Override
    public List<String> getStringList(Setting setting) {
        return this.app.getConfig().getStringList(setting.path);
    }

    @Override
    public void set(Setting setting, Object value) {
        this.app.getConfig().set(setting.path, value);
    }

    public File getFile(String file) {
        return new File(this.app.getDataFolder(), file);
    }

    public File getFile(File folder, String file) {
        return new File(folder, file);
    }

    @Override
    @Nullable
    public File getFolder(String folder) {
        File newFolder = new File(this.app.getDataFolder(), folder);
        if (!newFolder.exists()) {
            if (newFolder.mkdir()) return newFolder;
            else return null;
        } else {
            if (newFolder.isDirectory()) return newFolder;
            else return null;
        }
    }

    @Override
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
    @Override
    public FileConfiguration readResource(String file) {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(this.app.getResource(file)));
    }

    /**
     * Reads and loads a config
     *
     * @param file - The filename of the file
     * @return FileConfiguration - The file config
     */
    @Override
    public FileConfiguration readFile(String file) {
        return this.readFile(this.getFile(file));
    }

    public FileConfiguration readFile(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Returns a file with updated values from the default resource if they are missing.
     *
     * @param file
     */
    @Override
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
                config.save(new File(this.app.getDataFolder(), file));
            } catch (Exception e) {
                this.logger.severe(String.format("Couldn't save config with new values: %s", file));
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
    @Override
    public void saveFile(FileConfiguration file, String fileName) {
        try {
            file.save(new File(this.app.getDataFolder(), fileName));
        } catch (Exception e) {
            this.logger.severe("Couldn't handle " + fileName + " :" + e.getMessage());
        }
    }
}
