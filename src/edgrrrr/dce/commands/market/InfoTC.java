package edgrrrr.dce.commands.market;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * tab completer for the info command
 */
public class InfoTC implements TabCompleter {
    private final DCEPlugin app;

    public InfoTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_INFO_ENABLE_BOOLEAN.path))) {
            return null;
        }

        String[] strings;
        switch (args.length) {
            // 1 args
            // return names of players starting with arg
            case 1:
                strings = this.app.getMaterialManager().getMaterialNames(args[0]);
                break;

            // else
            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}
