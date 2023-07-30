package me.edgrrrr.de.commands;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.console.LogLevel;
import me.edgrrrr.de.help.Help;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

/**
 * The default inherited class for all Divinity Commands
 */
public abstract class DivinityCommand implements CommandExecutor {
    protected final Help help;
    protected final boolean isEnabled;
    protected final boolean hasConsoleSupport;
    // Link to app
    // The help object for this command
    // Whether the command is enabled or not
    // Whether the command supports console input
    private final DEPlugin main;

    /**
     * Constructor
     *
     * @param main
     * @param registeredCommandName
     * @param hasConsoleSupport
     * @param commandSetting
     */
    public DivinityCommand(DEPlugin main, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        this.main = main;
        this.help = this.getMain().getHelpMan().get(registeredCommandName);
        this.hasConsoleSupport = hasConsoleSupport;
        this.isEnabled = this.getMain().getConfig().getBoolean(commandSetting.path);

        PluginCommand command;
        if ((command = this.getMain().getCommand(registeredCommandName)) == null) {
            this.getMain().getConsole().warn("Command Executor '%s' is incorrectly setup", registeredCommandName);
        } else {
            command.setExecutor(this);
            if (!this.getMain().getConfMan().getBoolean(Setting.IGNORE_COMMAND_REGISTRY_BOOLEAN))
                this.getMain().getConsole().info("Command %s registered", registeredCommandName);
        }
    }

    /**
     * The command event all user commands call upon send
     *
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
                _onPlayerCommand((Player) sender, args);
            } else {
                _onConsoleCommand(args);
            }
            return true;

        } catch (Exception e) {
            this.getMain().getConsole().send(CommandResponse.ErrorOnCommand.defaultLogLevel, CommandResponse.ErrorOnCommand.message, command, e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    /**
     * The pre-handling of onPlayerCommand
     * Checks the command is enabled
     *
     * @param sender
     * @param args
     * @return
     */
    public boolean _onPlayerCommand(Player sender, String[] args) {
        if (!this.isEnabled) {
            this.getMain().getConsole().send(sender, CommandResponse.PlayerCommandIsDisabled.defaultLogLevel, CommandResponse.PlayerCommandIsDisabled.message);
            return true;
        } else {
            return this.onPlayerCommand(sender, args);
        }
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    public abstract boolean onPlayerCommand(Player sender, String[] args);

    /**
     * The pre-handling of the onConsoleCommand
     * Checks the command is enabled and has console support
     *
     * @param args
     * @return
     */
    public boolean _onConsoleCommand(String[] args) {
        if (!this.isEnabled) {
            this.getMain().getConsole().send(CommandResponse.ConsoleCommandIsDisabled.defaultLogLevel, CommandResponse.ConsoleCommandIsDisabled.message);
            return true;
        } else if (!this.hasConsoleSupport) {
            this.getMain().getConsole().send(CommandResponse.ConsoleSupportNotAdded.defaultLogLevel, CommandResponse.ConsoleSupportNotAdded.message);
            return true;
        } else {
            return this.onConsoleCommand(args);
        }
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    public abstract boolean onConsoleCommand(String[] args);

    /**
     * Returns the main module
     *
     * @return
     */
    public DEPlugin getMain() {
        return this.main;
    }

    /**
     * Default Message for standardized messaging across the commands.
     * Some may require string formatting to complete.
     */
    public enum CommandResponse {
        // Defaults
        ConsoleCommandIsDisabled("This command is not enabled.", LogLevel.WARNING),
        ConsoleSupportNotAdded("This command does not support the console.", LogLevel.WARNING),
        PlayerCommandIsDisabled("This command has not been enabled by the server.", LogLevel.WARNING),
        ErrorOnCommand("Error on command (%s): %s.", LogLevel.SEVERE),

        // Market
        MaterialMarketIsDisabled("This command is not enabled because the materials market has been disabled.", LogLevel.WARNING),
        EnchantMarketIsDisabled("This command is not enabled because the enchant market has been disabled.", LogLevel.WARNING),

        // Ping
        PingResponse("Pong!", LogLevel.INFO),

        // Balance
        BalanceResponse("Balance: %s.", LogLevel.INFO),
        BalanceResponseOther("%s's balance: %s.", LogLevel.INFO),
        NothingToDisplay("Nothing to display yet.", LogLevel.INFO),

        //Stock
        StockCountChanged("Stock level changed from %d(%s) to %d(%s).", LogLevel.INFO),
        StockValueChanged("Stock price from %s(%d) to %s(%d).", LogLevel.INFO),

        //Mail


        // Poly
        InvalidNumberOfArguments("Invalid number of arguments.", LogLevel.WARNING),
        NothingToSellAfterSkipping("After skipping items (for various reasons), there is nothing left to sell.", LogLevel.WARNING),
        NothingToSell("There is nothing to sell.", LogLevel.WARNING),
        InvalidArguments("Invalid arguments.", LogLevel.WARNING),
        InvalidPlayerName("Invalid player name.", LogLevel.WARNING),
        InvalidAmountGiven("Invalid amount given.", LogLevel.WARNING),
        InvalidItemName("Invalid item name '%s'.", LogLevel.WARNING),
        InvalidEnchantName("Invalid enchant name '%s'.", LogLevel.WARNING),
        InvalidItemHeld("Invalid held item.", LogLevel.WARNING),
        InvalidInventorySpace("Missing inventory space %d/%d.", LogLevel.WARNING),
        InvalidStockAmount("Missing stock %d/%d.", LogLevel.WARNING),
        InvalidInventoryStock("Missing inventory stock %d/%d", LogLevel.WARNING),
        UnknownError("Unknown error.", LogLevel.WARNING);
        public final String message;
        public final LogLevel defaultLogLevel;

        CommandResponse(String message, LogLevel defaultLevel) {
            this.message = message;
            this.defaultLogLevel = defaultLevel;
        }
    }
}
