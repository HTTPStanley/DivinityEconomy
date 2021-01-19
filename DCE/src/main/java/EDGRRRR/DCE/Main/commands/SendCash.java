package EDGRRRR.DCE.Main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command executor for sending cashing between players
 */
public class SendCash implements CommandExecutor {
    private App app;

    public SendCash(App app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player)) {
            return true;
        }
        Player from = (Player) sender;

        // Ensure two args
        if (!(args.length == 2)) {
            getCon().warn(from, "Incorrect usage, see¬");
            return false;
        }
        // First arg should be the <to> players name
        String toName = args[0];
        // Second arg should be the <amount>
        Double amount = Double.parseDouble(args[1]);

        // Get player by <to> name
        Player to = get().getServer().getPlayer(toName);

        // Ensure player found or just not null in general.
        if (to == null) {
            getCon().warn(from, "Incorrect player name '" + toName + "', see¬");
            return false;
        }

        // Ensure not sending to self
        if (to.getUniqueId() == from.getUniqueId()) {
            getCon().warn(from, "You can't send money to yourself ¯\\_(ツ)_/¯");
            return true;
        }

        // And it just works.
        getEco().sendCash(from, to, amount);

        return true;
    }
}
