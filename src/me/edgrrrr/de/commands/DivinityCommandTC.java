package me.edgrrrr.de.commands;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The default inherited class for all Divinity Command Tab Completer
 */
public abstract class DivinityCommandTC implements TabCompleter {
    protected final boolean isEnabled;
    protected final boolean hasConsoleSupport;
    // Link to app
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
    public DivinityCommandTC(DEPlugin main, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        this.main = main;
        this.hasConsoleSupport = hasConsoleSupport;
        this.isEnabled = this.main.getConfig().getBoolean(commandSetting.path);

        PluginCommand command;
        if ((command = getMain().getCommand(registeredCommandName)) == null) {
            getMain().getConsole().warn("Command TabCompleter '%s' is incorrectly setup", registeredCommandName);
        } else {
            if (this.isEnabled)
                command.setTabCompleter(this);
            if (!getMain().getConfMan().getBoolean(Setting.IGNORE_COMMAND_REGISTRY_BOOLEAN))
                getMain().getConsole().info("CommandTC %s registered", registeredCommandName);
        }
    }

    protected DEPlugin getMain() {
        return this.main;
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
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (sender instanceof Player) {
                return this._onPlayerTabComplete((Player) sender, args);
            } else {
                return this._onConsoleTabComplete(args);
            }
        } catch (Exception e) {
            this.main.getConsole().send(LangEntry.GENERIC_ErrorOnCommand.logLevel, LangEntry.GENERIC_ErrorOnCommand.get(getMain()), this.getClass().getName(), e.getMessage());
            e.printStackTrace();
            return null;
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
    public List<String> _onPlayerTabComplete(Player sender, String[] args) {
        if (!this.isEnabled) {
            return null;
        } else {
            return this.onPlayerTabCompleter(sender, args);
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
    public abstract List<String> onPlayerTabCompleter(Player sender, String[] args);

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
        } else {
            return this.onConsoleTabCompleter(args);
        }
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    public abstract List<String> onConsoleTabCompleter(String[] args);
}
