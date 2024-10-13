package org.divinitycraft.divinityeconomy.commands;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.bukkit.entity.Player;

public abstract class DivinityCommandExperience extends DivinityCommand {
    protected final boolean marketIsEnabled;

    /**
     * Constructor
     *
     * @param app
     * @param registeredCommandName
     * @param hasConsoleSupport
     * @param commandSetting
     */
    public DivinityCommandExperience(DEPlugin app, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        super(app, registeredCommandName, hasConsoleSupport, commandSetting);
        this.marketIsEnabled = getMain().getConfig().getBoolean(Setting.MARKET_EXP_ENABLE_BOOLEAN.path);
    }

    @Override
    public boolean _onPlayerCommand(Player sender, String[] args) {
        if (!this.marketIsEnabled) {
            getMain().getConsole().send(sender, LangEntry.MARKET_ExperienceMarketIsDisabled.logLevel, LangEntry.MARKET_ExperienceMarketIsDisabled.get(getMain()));
            return true;
        }

        return super._onPlayerCommand(sender, args);
    }

    @Override
    public boolean _onConsoleCommand(String[] args) {
        if (!this.marketIsEnabled) {
            getMain().getConsole().send(LangEntry.MARKET_ExperienceMarketIsDisabled.logLevel, LangEntry.MARKET_ExperienceMarketIsDisabled.get(getMain()));
            return true;
        }

        return super._onConsoleCommand(args);
    }
}
