package edgrrrr.dce.commands;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import org.bukkit.entity.Player;

public abstract class DivinityCommandMarket extends DivinityCommand {
    protected final boolean marketIsEnabled;

    /**
     * Constructor
     *
     * @param app
     * @param registeredCommandName
     * @param hasConsoleSupport
     * @param commandSetting
     */
    public DivinityCommandMarket(DCEPlugin app, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        super(app, registeredCommandName, hasConsoleSupport, commandSetting);
        this.marketIsEnabled = this.app.getConfig().getBoolean(Setting.MARKET_MATERIALS_ENABLE_BOOLEAN.path);
    }

    @Override
    public boolean _onPlayerCommand(Player sender, String[] args) {
        if (!this.marketIsEnabled) {
            this.app.getConsole().send(sender, CommandResponse.MaterialMarketIsDisabled.defaultLogLevel, CommandResponse.MaterialMarketIsDisabled.message);
            return true;
        } else if (!this.isEnabled) {
            this.app.getConsole().send(sender, CommandResponse.PlayerCommandIsDisabled.defaultLogLevel, CommandResponse.PlayerCommandIsDisabled.message);
            return true;
        } else {
            return this.onPlayerCommand(sender, args);
        }
    }

    @Override
    public boolean _onConsoleCommand(String[] args) {
        if (!this.marketIsEnabled) {
            this.app.getConsole().send(CommandResponse.MaterialMarketIsDisabled.defaultLogLevel, CommandResponse.MaterialMarketIsDisabled.message);
            return true;
        } else if (!this.isEnabled) {
            this.app.getConsole().send(CommandResponse.ConsoleCommandIsDisabled.defaultLogLevel, CommandResponse.ConsoleCommandIsDisabled.message);
            return true;
        } else if (!this.hasConsoleSupport) {
            this.app.getConsole().send(CommandResponse.ConsoleSupportNotAdded.defaultLogLevel, CommandResponse.ConsoleSupportNotAdded.message);
            return true;
        } else {
            return this.onConsoleCommand(args);
        }
    }
}
