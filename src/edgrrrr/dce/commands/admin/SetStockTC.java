package edgrrrr.dce.commands.admin;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the setvalue command
 */
public class SetStockTC implements TabCompleter {
    private final DCEPlugin app;

    public SetStockTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_SET_STOCK_ENABLE_BOOLEAN.path()))) {
            return null;
        }

        String[] strings;
        switch (args.length) {
            // Args 1
            // get player names that start with args[0]
            case 1:
                strings = this.app.getMaterialManager().getMaterialNames();
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}
