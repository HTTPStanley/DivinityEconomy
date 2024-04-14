package me.edgrrrr.de.commands;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The default inherited class for all Divinity Command Tab Completer
 */
public abstract class DivinityCommandMaterialsTC extends DivinityCommandTC {
    protected final boolean marketIsEnabled;

    /**
     * Constructor
     *
     * @param app
     * @param hasConsoleSupport
     * @param commandSetting
     */
    public DivinityCommandMaterialsTC(DEPlugin app, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        super(app, registeredCommandName, hasConsoleSupport, commandSetting);
        this.marketIsEnabled = getMain().getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path);
    }

    /**
     * The pre-handling of onPlayerCommand
     * Checks the command is enabled
     *
     * @param sender
     * @param args
     * @return
     */
    public List<String> _onPlayerTabComplete(Player sender, String[] args) {
        if (!this.isEnabled) {
            return null;
        } else if (!this.marketIsEnabled) {
            return null;
        } else {
            return this.onPlayerTabCompleter(sender, args);
        }
    }

    /**
     * The pre-handling of the onConsoleCommand
     * Checks the command is enabled and has console support
     *
     * @param args
     * @return
     */
    public List<String> _onConsoleTabComplete(String[] args) {
        if (!this.isEnabled) {
            return null;
        } else if (!this.hasConsoleSupport) {
            return null;
        } else if (!this.marketIsEnabled) {
            return null;
        } else {
            return this.onConsoleTabCompleter(args);
        }
    }
}
