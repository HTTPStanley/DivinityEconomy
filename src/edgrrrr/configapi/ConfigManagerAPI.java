package edgrrrr.configapi;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public interface ConfigManagerAPI {

    /**
     * Returns an object from the config
     */
    public Object get(Setting setting);

    /**
     * Returns an int from the config
     */
    public Integer getInt(Setting setting);

    /**
     * Returns a boolean from the config
     */
    public Boolean getBoolean(Setting setting);

    /**
     * Returns a double from the config
     */
    public Double getDouble(Setting setting);

    /**
     * Returns a string from the config
     */
    public String getString(Setting setting);

    /**
     * Returns a List containing Strings
     */
    public List<String> getStringList(Setting setting);

    /**
     * Sets a config value to the given object.
     */
    public void set(Setting setting, Object value);

    public File getFile(String file);

    public File getFile(File folder, String file);

    public File getFolder(String folder);

    public List<File> getFolderFiles(String folder);

    /**
     * Returns a jar resource
     */
    public FileConfiguration readResource(String file);

    /**
     * Returns a file config
     */
    public FileConfiguration readFile(String file);

    public FileConfiguration readFile(File file);

    /**
     * Returns a file with updated values from the default resource if they are missing.
     */
    public FileConfiguration loadFile(String file);

    /**
     * Saves a file to a location
     */
    public void saveFile(FileConfiguration file, String filename);
}
