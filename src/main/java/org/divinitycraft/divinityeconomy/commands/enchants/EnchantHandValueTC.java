package org.divinitycraft.divinityeconomy.commands.enchants;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandEnchantTC;
import org.divinitycraft.divinityeconomy.config.Setting;
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
    public EnchantHandValueTC(DEPlugin app) {
        super(app, "ehandvalue", false, Setting.COMMAND_E_VALUE_ENABLE_BOOLEAN);
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
