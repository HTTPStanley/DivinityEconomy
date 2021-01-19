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

        // Ensure two or more args
        if (!(args.length == 1)) {
            app.getCon().warn(from, "Incorrect usage, see¬");
            return false;
        }

        // Adds 2nd arg for set cash
        String[] argv = {args[0], "0"};

        CommandExecutor cashSetter = new SetBal(this.app);
        boolean response = cashSetter.onCommand(sender, command, label, argv);
        return response;
    }
}

