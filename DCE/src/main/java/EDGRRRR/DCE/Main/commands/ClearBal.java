package EDGRRRR.DCE.Main.commands;

import EDGRRRR.DCE.Main.App;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class ClearBal implements CommandExecutor {
    private App app;
    private String usage = "/clearbal <username> or /clearbal";

    public ClearBal(App app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;
        String to = null;

        switch (args.length) {
            case 0:
                to = from.getName();
                break;

            case 1:
                to = args[0];
                break;

            default:
                app.getCon().usage(from, "Incorrect number of arguments.", usage);;
                return true;
        }

        String[] argv = {to, "0"};
        return app.getCommandSetBal().onCommand(sender, command, label, argv);
    }
}

