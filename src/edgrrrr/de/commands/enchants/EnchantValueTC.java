package edgrrrr.de.commands.enchants;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandEnchantTC;
import edgrrrr.de.config.Setting;
import edgrrrr.de.enchants.EnchantData;
import edgrrrr.de.utils.ArrayUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the enchant hand value command
 */
public class EnchantValueTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantValueTC(DEPlugin app) {
        super(app, true, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public List<String> onPlayerTabCompleter(Player sender, String[] args) {
        String[] strings;
        EnchantData enchantData;
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1:
                strings = this.app.getEnchantmentManager().getEnchantNames(args[0]);
                break;

            // 2 args
            // return max stack size for the material given
            case 2:
                enchantData = this.app.getEnchantmentManager().getEnchant(args[0]);
                int maxLevel = 1;
                if (enchantData != null) {
                    maxLevel = enchantData.getMaxLevel();
                }

                strings = ArrayUtils.strRange(1, maxLevel);
                break;

            // else
            default:
                strings = this.app.getEnchantmentManager().getEnchantNames();
                break;
        }

        return Arrays.asList(strings);
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        return this.onPlayerTabCompleter(null, args);
    }
}
