package org.divinitycraft.divinityeconomy.commands.help;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.help.Help;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
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
    public HelpCommand(DEPlugin app) {
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
        int pageNumber = 0;
        String term = "";

        switch (args.length) {
            case 0:
                break;

            case 1:
                pageNumber = Converter.getInt(args[0]);
                if (pageNumber == 0) {
                    term = args[0];
                }
                break;

            case 2:
                term = args[0];
                pageNumber = Converter.getInt(args[1]);
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                break;
        }

        // Get help
        Help help = getMain().getHelpMan().get(term);

        // If help is not null, show help
        if (help != null) {
            getMain().getConsole().help(sender, help.getCommand(), help.getDescription(), help.getUsages(), help.getAliases());
            return true;
        }

        // Else get pages based on search term
        Map<Integer, List<Help>> helpPages = getMain().getHelpMan().getPages(term);

        // If page number is 0, set to 1
        if (pageNumber == 0) {
            pageNumber = 1;
        }


        // If map contains number, show page
        if (helpPages.containsKey(pageNumber - 1)) {
            this.showPage(sender, pageNumber, helpPages.size(), term, helpPages.get(pageNumber - 1));
            return true;
        }

        // if page number is greater than max pages, show last page
        if ((helpPages.size()) <= pageNumber - 1) {
            this.showPage(sender, helpPages.size(), helpPages.size(), term, helpPages.get(helpPages.size() - 1));
            return true;
        }

        // Else, show first page
        this.showPage(sender, 1, helpPages.size(), term, helpPages.get(0));
        return true;
    }

    public void showPage(Player sender, int pageNumber, int maxPages, String term, List<Help> help) {
        // Define title
        String title = LangEntry.HELP_Header.get(getMain());
        String pageNumberString = String.format("%s%s%s", ChatColor.AQUA, pageNumber, ChatColor.GREEN);
        String maxPagesString = String.format("%s%s%s", ChatColor.AQUA, maxPages, ChatColor.GREEN);

        // Show help without search term
        if (term.isEmpty()) {
            getMain().getConsole().info(sender, title, ChatColor.GREEN, pageNumberString, maxPagesString, ChatColor.GREEN);
        }

        // Show help with search term
        else {
            String termString = String.format("%s'%s'%s", ChatColor.AQUA, term, ChatColor.GREEN);
            getMain().getConsole().info(sender, title, ChatColor.GREEN, pageNumberString, maxPagesString, termString);
        }

        // Padding
        getMain().getConsole().info(sender, "");

        // Show help
        if (help.isEmpty()) {
            getMain().getConsole().info(sender, LangEntry.HELP_NoneFound.get(getMain()));
            return;
        }
        for (Help helpCom : help) {
            getMain().getConsole().info(sender, "%s%s%s: %s%s", ChatColor.AQUA, helpCom.getCommand(), ChatColor.WHITE, ChatColor.GREEN, helpCom.getDescription(64));
        }
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
