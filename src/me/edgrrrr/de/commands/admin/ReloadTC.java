package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A tab completer for the reload command
 */
public class ReloadTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public ReloadTC(DEPlugin app) {
        super(app, "reload", true, Setting.COMMAND_RELOAD_ENABLE_BOOLEAN);
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
        return onConsoleTabCompleter(args);
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        List<String> completions = new ArrayList<>();
        LangEntry.W_config.addLang(getMain(), completions);
        LangEntry.W_materials.addLang(getMain(), completions);
        LangEntry.W_enchants.addLang(getMain(), completions);
        LangEntry.W_potions.addLang(getMain(), completions);
        LangEntry.W_entities.addLang(getMain(), completions);
        LangEntry.W_experience.addLang(getMain(), completions);
        return completions;
    }
}
