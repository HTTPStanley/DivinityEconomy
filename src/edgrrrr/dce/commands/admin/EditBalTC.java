package edgrrrr.dce.commands.admin;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * tab completer for editbal command
 */
public class EditBalTC implements TabCompleter {
    private final DCEPlugin app;

    public EditBalTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_EDIT_BALANCE_ENABLE_BOOLEAN.path()))) {
            return null;
        }

        String[] strings;
        switch (args.length) {
            // Args 1
            // get player names that start with args[0]
            case 1:
                strings = this.app.getPlayerManager().getOfflinePlayersNames(args[0]);
                break;

            // Args 2
            // just return some numbers
            case 2:
                strings = new String[]{
                    "-1000", "-100", "-10", "-1",
                    "1000", "100", "10", "1"
                };
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}
