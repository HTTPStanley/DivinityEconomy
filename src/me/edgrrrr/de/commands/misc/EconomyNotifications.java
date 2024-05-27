package me.edgrrrr.de.commands.misc;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.economy.players.EconomyPlayer;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class EconomyNotifications extends DivinityCommand {
    /**
     * Constructor
     *
     * @param app
     */
    public EconomyNotifications(DEPlugin app) {
        super(app, "economynotifications", true, Setting.COMMAND_ECONOMY_NOTIFICATIONS_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        // Get the player's economy object
        EconomyPlayer player = this.getMain().getEconMan().getPlayer(sender);
        boolean enable;

        // Check args
        switch (args.length) {
            case 0:
                enable = !player.getNotification();
                break;

            case 1:
                enable = Converter.getBoolean(args[0]);
                break;

            default:
                getMain().getConsole().usage(LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Toggle status
        player.setNotification(enable);

        // Send a message
        this.getMain().getConsole().info(sender, LangEntry.MISC_EnableNotifications.get(this.getMain(), player.getNotification()));
        return true;
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        OfflinePlayer player = null;
        Boolean enable = null;
        switch (args.length) {
            case 1:
                // Get the player's economy object
                player = this.getMain().getPlayMan().getPlayer(args[0], true);
                break;

            case 2:
                player = this.getMain().getPlayMan().getPlayer(args[0], true);
                enable = Converter.getBoolean(args[1]);
                break;


            default:
                getMain().getConsole().usage(LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        // Check if the player is null
        if (player == null) {
            getMain().getConsole().warn(LangEntry.GENERIC_InvalidPlayerName.get(getMain()), args[0]);
            return true;
        }


        // Get the player's economy object
        EconomyPlayer econPlayer = this.getMain().getEconMan().getPlayer(player);


        // Check if enable is null
        if (enable == null) {
            enable = !econPlayer.getNotification();
        }


        // Toggle status
        econPlayer.setNotification(enable);


        // Send a message
        this.getMain().getConsole().info(LangEntry.MISC_EnableNotificationsFor.get(this.getMain(), econPlayer.getName(), econPlayer.getNotification()));
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            this.getMain().getConsole().info(player.getPlayer(), LangEntry.MISC_EnableNotifications.get(this.getMain(), econPlayer.getNotification()));
        }

        return true;
    }
}
