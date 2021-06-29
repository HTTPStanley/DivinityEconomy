package me.edgrrrr.de.commands;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
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
        this.marketIsEnabled = this.getMain().getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path);
    }

    @Override
    public boolean _onPlayerCommand(Player sender, String[] args) {
        if (!this.marketIsEnabled) {
            this.getMain().getConsole().send(sender, CommandResponse.MaterialMarketIsDisabled.defaultLogLevel, CommandResponse.MaterialMarketIsDisabled.message);
            return true;
        } else if (!this.isEnabled) {
            this.getMain().getConsole().send(sender, CommandResponse.PlayerCommandIsDisabled.defaultLogLevel, CommandResponse.PlayerCommandIsDisabled.message);
            return true;
        } else {
            return this.onPlayerCommand(sender, args);
        }
    }

    @Override
    public boolean _onConsoleCommand(String[] args) {
        if (!this.marketIsEnabled) {
            this.getMain().getConsole().send(CommandResponse.MaterialMarketIsDisabled.defaultLogLevel, CommandResponse.MaterialMarketIsDisabled.message);
            return true;
        } else if (!this.isEnabled) {
            this.getMain().getConsole().send(CommandResponse.ConsoleCommandIsDisabled.defaultLogLevel, CommandResponse.ConsoleCommandIsDisabled.message);
            return true;
        } else if (!this.hasConsoleSupport) {
            this.getMain().getConsole().send(CommandResponse.ConsoleSupportNotAdded.defaultLogLevel, CommandResponse.ConsoleSupportNotAdded.message);
            return true;
        } else {
            return this.onConsoleCommand(args);
        }
    }
}
