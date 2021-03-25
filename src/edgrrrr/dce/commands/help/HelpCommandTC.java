package edgrrrr.dce.commands.help;

import edgrrrr.dce.DCEPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class HelpCommandTC implements TabCompleter {
    DCEPlugin app;

    public HelpCommandTC(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        Player player;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        } else {
            player = null;
        }

        String[] strings;

        switch (args.length) {
            case 1:
                ArrayList<String> allStrings = new ArrayList<>();
                for (Integer i : this.app.getHelpManager().getPages(8).keySet()) {
                    allStrings.add(String.valueOf(i+1));
                }
                allStrings.addAll(Arrays.asList(this.app.getHelpManager().getAllNames(args[0])));
                strings = allStrings.toArray(new String[0]);
                break;

            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }
}
