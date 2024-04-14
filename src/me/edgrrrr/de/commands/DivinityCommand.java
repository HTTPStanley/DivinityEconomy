package me.edgrrrr.de.commands;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.help.Help;
import me.edgrrrr.de.lang.LangEntry;
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
        this.help = getMain().getHelpMan().get(registeredCommandName);
        this.hasConsoleSupport = hasConsoleSupport;
        this.isEnabled = getMain().getConfig().getBoolean(commandSetting.path);

        PluginCommand command;
        if ((command = getMain().getCommand(registeredCommandName)) == null) {
            getMain().getConsole().warn("Command Executor '%s' is incorrectly setup", registeredCommandName);
        } else {
            command.setExecutor(this);
            if (!getMain().getConfMan().getBoolean(Setting.IGNORE_COMMAND_REGISTRY_BOOLEAN))
                getMain().getConsole().info("Command %s registered", registeredCommandName);
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
            getMain().getConsole().send(LangEntry.GENERIC_ErrorOnCommand.logLevel, LangEntry.GENERIC_ErrorOnCommand.get(getMain()), command, e.getMessage());
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
            getMain().getConsole().send(sender, LangEntry.GENERIC_PlayerCommandIsDisabled.logLevel, LangEntry.GENERIC_PlayerCommandIsDisabled.get(getMain()));
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
            getMain().getConsole().send(LangEntry.GENERIC_ConsoleCommandIsDisabled.logLevel, LangEntry.GENERIC_ConsoleCommandIsDisabled.get(getMain()));
            return true;
        } else if (!this.hasConsoleSupport) {
            getMain().getConsole().send(LangEntry.GENERIC_ConsoleSupportNotAdded.logLevel, LangEntry.GENERIC_ConsoleSupportNotAdded.get(getMain()));
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
}
