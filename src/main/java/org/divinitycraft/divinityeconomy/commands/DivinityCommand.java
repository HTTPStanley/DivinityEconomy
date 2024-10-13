package org.divinitycraft.divinityeconomy.commands;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.help.Help;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
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
    protected boolean checkEconomyEnabled = false;
    protected boolean checkEnchantMarketEnabled = false;
    protected boolean checkItemMarketEnabled = false;
    protected boolean checkExperienceMarketEnabled = false;
    protected boolean checkPermissions = true;
    private final PluginCommand command;
    private final DEPlugin main;

    /**
     * Constructor
     *
     * @param main
     * @param registeredCommandName
     * @param hasConsoleSupport
     * @param isEnabled
     */
    public DivinityCommand(DEPlugin main, String registeredCommandName, boolean hasConsoleSupport, boolean isEnabled) {
        this.main = main;
        this.help = getMain().getHelpMan().get(registeredCommandName);
        this.hasConsoleSupport = hasConsoleSupport;
        this.isEnabled = isEnabled;

        if ((command = getMain().getCommand(registeredCommandName)) == null) {
            getMain().getConsole().warn("Command Executor '%s' is incorrectly setup", registeredCommandName);
        } else {
            command.setExecutor(this);
            if (!getMain().getConfMan().getBoolean(Setting.IGNORE_COMMAND_REGISTRY_BOOLEAN))
                getMain().getConsole().info("Command %s registered", registeredCommandName);
        }
    }

    /**
     * Constructor
     *
     * @param main
     * @param registeredCommandName
     * @param hasConsoleSupport
     * @param commandSetting
     */
    public DivinityCommand(DEPlugin main, String registeredCommandName, boolean hasConsoleSupport, Setting commandSetting) {
        this(main, registeredCommandName, hasConsoleSupport, main.getConfMan().getBoolean(commandSetting));
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
            returnCommandDisabled(sender);
            return true;
        } else if (checkPermissions && !command.testPermission(sender)) {
            returnPlayerNoPermission(sender);
            return true;
        }
        else if (this.checkEconomyEnabled && !this.checkEconomyEnabledInWorld(sender)) {
            getMain().getConsole().send(sender, LangEntry.WORLDS_EconomyDisabledInThisWorld.logLevel, LangEntry.WORLDS_EconomyDisabledInThisWorld.get(getMain()));
            return true;
        } else if ((this.checkItemMarketEnabled || this.checkEnchantMarketEnabled || this.checkExperienceMarketEnabled) && !this.checkMarketEnabledInWorld(sender)) {
            getMain().getConsole().send(sender, LangEntry.WORLDS_MarketDisabledInThisWorld.logLevel, LangEntry.WORLDS_MarketDisabledInThisWorld.get(getMain()));
            return true;
        }  else if (this.checkItemMarketEnabled && !this.checkItemMarketEnabledInWorld(sender)) {
            getMain().getConsole().send(sender, LangEntry.WORLDS_ItemMarketDisabledInThisWorld.logLevel, LangEntry.WORLDS_ItemMarketDisabledInThisWorld.get(getMain()));
            return true;
        } else if (this.checkEnchantMarketEnabled && !this.checkEnchantMarketEnabledInWorld(sender)) {
            getMain().getConsole().send(sender, LangEntry.WORLDS_EnchantMarketDisabledInThisWorld.logLevel, LangEntry.WORLDS_EnchantMarketDisabledInThisWorld.get(getMain()));
            return true;
        } else if (this.checkExperienceMarketEnabled && !this.checkExperienceMarketEnabledInWorld(sender)) {
            getMain().getConsole().send(sender, LangEntry.WORLDS_ExperienceMarketDisabledInThisWorld.logLevel, LangEntry.WORLDS_ExperienceMarketDisabledInThisWorld.get(getMain()));
        }

        return this.onPlayerCommand(sender, args);
    }

    /**
     * Returns the command is disabled message
     *
     * @param sender
     */
    public void returnCommandDisabled(Player sender) {
        getMain().getConsole().send(sender, LangEntry.GENERIC_PlayerCommandIsDisabled.logLevel, LangEntry.GENERIC_PlayerCommandIsDisabled.get(getMain()));
    }


    /**
     * Returns the player has no permission message
     *
     * @param sender
     */
    public void returnPlayerNoPermission(Player sender) {
        getMain().getConsole().send(sender, LangEntry.GENERIC_PlayerNoPermission.logLevel, LangEntry.GENERIC_PlayerNoPermission.get(getMain()));
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
            returnCommandDisabled(null);
            return true;
        } else if (!this.hasConsoleSupport) {
            getMain().getConsole().send(LangEntry.GENERIC_ConsoleSupportNotAdded.logLevel, LangEntry.GENERIC_ConsoleSupportNotAdded.get(getMain()));
            return true;
        }

        return this.onConsoleCommand(args);
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
     * Check if the economy is enabled in the world
     * @param player
     * @return
     */
    public boolean checkEconomyEnabledInWorld(Player player) {
        return getMain().getWorldMan().isEconomyEnabled(player.getWorld());
    }


    /**
     * Check if the market is enabled in the world
     * @param player
     * @return
     */
    public boolean checkMarketEnabledInWorld(Player player) {
        return getMain().getWorldMan().isMarketEnabled(player.getWorld());
    }


    /**
     * Check if the enchant market is enabled in the world
     * @param player
     * @return
     */
    public boolean checkEnchantMarketEnabledInWorld(Player player) {
        return getMain().getWorldMan().isEnchantMarketEnabled(player.getWorld());
    }


    /**
     * Check if the item market is enabled in the world
     * @param player
     * @return
     */
    public boolean checkItemMarketEnabledInWorld(Player player) {
        return getMain().getWorldMan().isItemMarketEnabled(player.getWorld());
    }


    /**
     * Check if the experience market is enabled in the world
     * @param player
     * @return
     */
    public boolean checkExperienceMarketEnabledInWorld(Player player) {
        return getMain().getWorldMan().isExperienceMarketEnabled(player.getWorld());
    }


    /**
     * Returns the main module
     *
     * @return
     */
    public DEPlugin getMain() {
        return this.main;
    }
}
