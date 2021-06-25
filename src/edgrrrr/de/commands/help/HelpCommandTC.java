package edgrrrr.de.commands.help;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommandTC;
import edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommandTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public HelpCommandTC(DEPlugin app) {
        super(app, true, Setting.COMMAND_EHELP_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public List<String> onPlayerTabCompleter(Player sender, String[] args) {
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

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        return this.onPlayerTabCompleter(null, args);
    }
}
