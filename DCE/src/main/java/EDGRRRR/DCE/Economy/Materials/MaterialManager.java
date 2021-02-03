package EDGRRRR.DCE.Economy.Materials;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;

import EDGRRRR.DCE.Main.DCEPlugin;

public class MaterialManager {
    // Link back to Main
    private DCEPlugin app;

    // Stores items
    private HashMap<String, String> aliases;
    private HashMap<String, Object> materials;

    // Stores the default items.json file location
    private String materialsFile = "materials.yml";
    private String aliasesFile = "aliases.yml";


    /**
     * Constructor
     * You will likely need to call loadMaterials and loadAliases to populate the aliases and materials with data from the program.
     * @param app
     */
    public MaterialManager(DCEPlugin app) {
        this.app = app;
    }

    public void loadMaterials() {
        FileConfiguration config = loadFile(materialsFile);
        HashMap<String, Object> values = new HashMap<String, Object>();
        for (String key : config.getKeys(true)) {
            String value = config.getString(key);
            values.put(key, value);
        }
        this.materials = values;
        app.getCon().info("Loaded " + values.size() + " materials from " + materialsFile);
    }

    public void loadAliases() {
        FileConfiguration config = loadFile(aliasesFile);
        HashMap<String, String> values = new HashMap<String, String>();
        for (String key : config.getKeys(true)) {
            String value = config.getString(key);
            values.put(key, value);
        }
        this.aliases = values;
        app.getCon().info("Loaded " + values.size() + " aliases from " + aliasesFile);
    }

    public FileConfiguration loadFile(String file) {
        // Instantiate default and user config
        FileConfiguration defConfig = null;
        FileConfiguration config = null;
        try {
            // Load default and user config
            defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(app.getResource(file)));
            config = YamlConfiguration.loadConfiguration(new File(app.getDataFolder(), file));

            // If config is empty, overwrite with defaults
            // Empty can either mean non-existent or empty file.
            if (config.getValues(true).size() == 0) {
                config.setDefaults(defConfig);
                config.options().copyDefaults(true);
                config.save(new File(app.getDataFolder(), file));
            }
        } catch (Exception e) {
            // I don't know why this would happen but ¯\_(ツ)_/¯
            app.getCon().severe("Couldn't handle " + file + " :" + e.getMessage());
            app.getServer().getPluginManager().disablePlugin(app);
        }

        return config;        
    }

}
