package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for editing (adding or removing) cash to a player
 */
public class ClearBal implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/clearbal <username> | /clearbal";

    public ClearBal(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }

        Player from = (Player) sender;
        String to;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComClearBal))) {
            this.app.getConsoleManager().severe(from, "This command is not enabled.");
            return true;
        }

        switch (args.length) {
            case 0:
                to = from.getName();
                break;

            case 1:
                to = args[0];
                break;

            default:
                this.app.getConsoleManager().usage(from, "Incorrect number of arguments.", usage);
                return true;
        }

        String[] argv = {to, "0"};
        return this.app.getCommandSetBal().onCommand(sender, command, label, argv);
    }
}

