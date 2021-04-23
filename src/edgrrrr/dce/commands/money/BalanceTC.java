package edgrrrr.dce.commands.money;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the balance command
 */
public class BalanceTC implements TabCompleter {
    private final DCEPlugin app;

    public BalanceTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_BALANCE_ENABLE_BOOLEAN.path))) {
            return null;
        }

        String[] playerNames;
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1:
                playerNames = this.app.getPlayerManager().getOfflinePlayerNames(args[0]);
                break;

            default:
                playerNames = new String[0];
                break;
        }

        return Arrays.asList(playerNames);
    }
}
