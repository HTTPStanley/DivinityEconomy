package edgrrrr.dce.commandTabCompletions.admin;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ClearBalTC implements TabCompleter {
    private final DCEPlugin app;

    public ClearBalTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_CLEAR_BALANCE_ENABLE_BOOLEAN.path()))) {
            return null;
        }

        String[] playerNames = new String[0];
        switch (args.length) {
            // 0 args
            // return names of players

            case 0:
                playerNames = this.app.getPlayerManager().getOfflinePlayers();
                break;


            // 1 args
            // return names of players starting with arg
            case 1:
                playerNames = this.app.getPlayerManager().getOfflinePlayers(args[0]);
                break;

            default:
                break;
        }

        return Arrays.asList(playerNames);
    }
}
