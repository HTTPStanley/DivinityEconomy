package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Tab completer for the clearbal command.
 */
public class ClearBalTC implements TabCompleter {
    private final DCEPlugin app;

    public ClearBalTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN.path))) {
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
