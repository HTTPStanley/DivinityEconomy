package me.edgrrrr.de.migrators.migrations;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.market.items.materials.potion.PotionManager;
import me.edgrrrr.de.migrators.Migration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionType;

import java.io.File;

/**
 * This class is a migration for version 3.4.3 to 3.4.4
 */
public class Migrate343 extends Migration {
    public Migrate343(DEPlugin main) {
        super(main, "3.4.3", "3.4.4");
    }

    @Override
    protected void migrate() {
        // Migrate the data
        this.migratePotionFile();
    }


    private void migratePotionFile() {
        // Migrate the potion file
        // We need to change all instances of:
        // POTION_EXTENDED: true
        // POTION_TYPE: POISON -> POTION_TYPE: LONG_POISON
        //
        // POTION_UPGRADED: true
        // POTION_TYPE: POISON -> POTION_TYPE: STRONG_POISON
        File potionFile = this.getMain().getConfMan().getFile(PotionManager.PotionFile);
        FileConfiguration potionConfig = this.getMain().getConfMan().readFile(potionFile);

        // Migrate the data
        for (String key : potionConfig.getKeys(false)) {
            String potionType = potionConfig.getString(key + ".POTION_TYPE");

            // Check if the potion is extended
            if (potionConfig.getBoolean(key + ".POTION_EXTENDED")) {
                potionConfig.set(key + ".POTION_TYPE", "LONG_" + potionType);
            }

            // Check if the potion is upgraded
            if (potionConfig.getBoolean(key + ".POTION_UPGRADED")) {
                potionConfig.set(key + ".POTION_TYPE", "STRONG_" + potionType);
            }

            // Remove the old keys
            potionConfig.set(key + ".POTION_EXTENDED", null);
            potionConfig.set(key + ".POTION_UPGRADED", null);

            // Check new potion type
            try {
                PotionType.valueOf(potionConfig.getString(key + ".POTION_TYPE"));
            } catch (IllegalArgumentException exception) {
                potionConfig.set(key, null);
                this.getMain().getConsole().migrate("Removed potion %s due to invalid potion type", key);
            }
        }

//        // Check all potions exist in the file
//        for (PotionType potionType : PotionType.values()) {
//            for (String materialID: new String[]{
//                    "POTION", "SPLASH_POTION", "LINGERING_POTION", "TIPPED_ARROW"
//            }) {
//                String key = potionType.name() + "_" + materialID;
//                if (potionConfig.getKeys(false).stream().noneMatch(k -> k.equals(key))) {
//                    this.getMain().getConsole().migrate("Adding potion %s to the potion file", potionType.name());
//
//                    potionConfig.set(key + ".ALLOWED", false);
//                    potionConfig.set(key + ".MATERIAL_ID", materialID);
//                    potionConfig.set(key + ".POTION_TYPE", potionType.name());
//                    potionConfig.set(key + ".QUANTITY", 0);
//                }
//            }
//        }

        // Save
        this.getMain().getConfMan().saveFile(potionConfig, PotionManager.PotionFile);
    }
}
