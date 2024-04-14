package me.edgrrrr.de.commands;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.entity.Player;

public abstract class DivinityCommandMaterials extends DivinityCommand {
    protected final boolean marketIsEnabled;

    /**
     * Constructor
     *
     * @param app
     * @param registeredCommandName
     * @param hasConsoleSupport
     * @param commandSetting
     */
    public DivinityCommandMaterials(DEPlugin app, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        super(app, registeredCommandName, hasConsoleSupport, commandSetting);
        this.marketIsEnabled = getMain().getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path);
    }

    @Override
    public boolean _onPlayerCommand(Player sender, String[] args) {
        if (!this.marketIsEnabled) {
            getMain().getConsole().send(sender, LangEntry.MARKET_MaterialMarketIsDisabled.logLevel, LangEntry.MARKET_MaterialMarketIsDisabled.get(getMain()));
            return true;
        } else if (!this.isEnabled) {
            getMain().getConsole().send(sender, LangEntry.GENERIC_PlayerCommandIsDisabled.logLevel, LangEntry.GENERIC_PlayerCommandIsDisabled.get(getMain()));
            return true;
        } else {
            return this.onPlayerCommand(sender, args);
        }
    }

    @Override
    public boolean _onConsoleCommand(String[] args) {
        if (!this.marketIsEnabled) {
            getMain().getConsole().send(LangEntry.MARKET_MaterialMarketIsDisabled.logLevel, LangEntry.MARKET_MaterialMarketIsDisabled.get(getMain()));
            return true;
        } else if (!this.isEnabled) {
            getMain().getConsole().send(LangEntry.GENERIC_ConsoleCommandIsDisabled.logLevel, LangEntry.GENERIC_ConsoleCommandIsDisabled.get(getMain()));
            return true;
        } else if (!this.hasConsoleSupport) {
            getMain().getConsole().send(LangEntry.GENERIC_ConsoleSupportNotAdded.logLevel, LangEntry.GENERIC_ConsoleSupportNotAdded.get(getMain()));
            return true;
        } else {
            return this.onConsoleCommand(args);
        }
    }
}
