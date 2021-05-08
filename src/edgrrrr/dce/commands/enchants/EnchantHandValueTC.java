package edgrrrr.dce.commands.enchants;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandEnchantTC;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A tab completer for the enchant hand value command
 */
public class EnchantHandValueTC extends DivinityCommandEnchantTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EnchantHandValueTC(DCEPlugin app) {
        super(app, false, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
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
        return null;
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        return null;
    }
}
