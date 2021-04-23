package edgrrrr.dce.commands;

import edgrrrr.configapi.Setting;
import edgrrrr.consoleapi.LogLevel;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The default inherited class for all Divinity Commands
 */
public abstract class DivinityCommand implements CommandExecutor {
    // Link to app
    // The help object for this command
    // Whether the command is enabled or not
    // Whether the command supports console input
    protected final DCEPlugin app;
    protected final Help help;
    protected final boolean isEnabled;
    protected final boolean hasConsoleSupport;

    /**
     * Default Message for standardized messaging across the commands.
     * Some may require string formatting to complete.
     */
    protected enum Message {
        // Defaults
        ConsoleCommandIsDisabled("This command is not enabled.", LogLevel.WARNING),
        ConsoleSupportNotAdded("This command does not support the console.", LogLevel.WARNING),
        PlayerCommandIsDisabled("This command has not been enabled by the server.", LogLevel.WARNING),
        ErrorOnCommand("Error on command (%s): %s.", LogLevel.SEVERE),

        // Ping
        PingResponse("Pong!", LogLevel.INFO),

        // Balance
        BalanceResponse("Balance: £%,.2f.", LogLevel.INFO),
        BalanceResponseOther("%s's balance: £%,.2f.", LogLevel.INFO),

        // Poly
        InvalidNumberOfArguments("Invalid number of arguments.", LogLevel.WARNING),
        InvalidPlayerNameResponse("Invalid player name.", LogLevel.WARNING)
        ;
        public String message;
        public LogLevel defaultLogLevel;
        Message(String message, LogLevel defaultLevel) {
            this.message = message;
            this.defaultLogLevel = defaultLevel;
        }
    }

    /**
     * Constructor
     * @param app
     * @param registeredCommandName
     * @param hasConsoleSupport
     * @param commandSetting
     */
    public DivinityCommand(DCEPlugin app, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        this.app = app;
        this.help = this.app.getHelpManager().get(registeredCommandName);
        this.hasConsoleSupport = hasConsoleSupport;
        this.isEnabled = this.app.getConfig().getBoolean(commandSetting.path);
    }

    /**
     * The command event all user commands call upon send
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (sender instanceof Player) {
                return this._onPlayerCommand((Player) sender, args);
            } else {
                return this._onConsoleCommand(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.app.getConsole().send(Message.ErrorOnCommand.defaultLogLevel, String.format(Message.ErrorOnCommand.message, command, e.getMessage()));
            return false;
        }
    }

    /**
     * The pre-handling of onPlayerCommand
     * Checks the command is enabled
     * @param sender
     * @param args
     * @return
     */
    public boolean _onPlayerCommand(Player sender, String[] args){
        if (!this.isEnabled) {
            this.app.getConsole().send(sender, Message.PlayerCommandIsDisabled.defaultLogLevel, Message.PlayerCommandIsDisabled.message);
            return true;
        } else {
            return this.onPlayerCommand(sender, args);
        }
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     * @param sender
     * @param args
     * @return
     */
    public abstract boolean onPlayerCommand(Player sender, String[] args);

    /**
     * The pre-handling of the onConsoleCommand
     * Checks the command is enabled and has console support
     * @param args
     * @return
     */
    public boolean _onConsoleCommand(String[] args) {
        if (!this.isEnabled) {
            this.app.getConsole().send(Message.ConsoleCommandIsDisabled.defaultLogLevel, Message.ConsoleCommandIsDisabled.message);
            return true;
        } else if (!this.hasConsoleSupport) {
            this.app.getConsole().send(Message.ConsoleSupportNotAdded.defaultLogLevel, Message.ConsoleSupportNotAdded.message);
            return true;
        } else {
            return this.onConsoleCommand(args);
        }
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     * @param args
     * @return
     */
    public abstract boolean onConsoleCommand(String[] args);
}
