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
        }

        return super._onPlayerCommand(sender, args);
    }

    @Override
    public boolean _onConsoleCommand(String[] args) {
        if (!this.marketIsEnabled) {
            getMain().getConsole().send(LangEntry.MARKET_MaterialMarketIsDisabled.logLevel, LangEntry.MARKET_MaterialMarketIsDisabled.get(getMain()));
            return true;
        }

        return super._onConsoleCommand(args);
    }
}
