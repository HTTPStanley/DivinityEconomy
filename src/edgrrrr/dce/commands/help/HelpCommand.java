package edgrrrr.dce.commands.help;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.math.Math;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;

/**
 * A command for getting help
 */
public class HelpCommand implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public HelpCommand(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("ehelp");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            player = null;
        }

        Help help = null;
        int pageNumber = -1;

        switch(args.length) {
            case 0:
                pageNumber = 0;
                break;

            case 1:
                pageNumber = Math.getInt(args[0]);
                help =  this.app.getHelpManager().get(args[0]);
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Not enough arguments.", this.help);
                break;
        }

        Map<Integer, Help[]> helpPages = this.app.getHelpManager().getPages(8);
        if (help == null && !helpPages.containsKey(pageNumber-1)) {
            DCEPlugin.CONSOLE.usage(player, "invalid command or page number", this.help);

        } else {
            if (helpPages.containsKey(pageNumber-1)) {
                DCEPlugin.CONSOLE.info(player, String.format("Help page %s/%s", pageNumber, helpPages.size()));
                for (Help helpCom : helpPages.get(pageNumber-1)) {
                    DCEPlugin.CONSOLE.info(player, String.format("%s: %s...", helpCom.getCommand(), helpCom.getDescription(20)));
                }

            } else {
                DCEPlugin.CONSOLE.help(player, help);
            }
        }

        return true;
    }
}
