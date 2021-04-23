package edgrrrr.dce.commands.help;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommand;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.math.Math;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * A command for getting help
 */
public class HelpCommand extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public HelpCommand(DCEPlugin app) {
        super(app, "ehelp", true, Setting.COMMAND_EHELP_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
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
                this.app.getConsole().usage(sender, "Not enough arguments.", this.help.getUsages());
                break;
        }

        Map<Integer, Help[]> helpPages = this.app.getHelpManager().getPages(8);
        if (help == null && !helpPages.containsKey(pageNumber-1)) {
            this.app.getConsole().usage(sender, "invalid command or page number", this.help.getUsages());

        } else {
            int maxLength = 30;
            String string;
            if (helpPages.containsKey(pageNumber-1)) {
                this.app.getConsole().info(sender, String.format("Help page %s/%s", pageNumber, helpPages.size()));
                for (Help helpCom : helpPages.get(pageNumber-1)) {
                    this.app.getConsole().info(sender, String.format("%s: %s...", helpCom.getCommand(), helpCom.getDescription(20)));
                }

            } else {
                this.app.getConsole().help(sender, help.getCommand(), help.getDescription(), help.getUsages(), help.getAliases());
            }
        }

        return true;
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        return this.onPlayerCommand(null, args);
    }
}
