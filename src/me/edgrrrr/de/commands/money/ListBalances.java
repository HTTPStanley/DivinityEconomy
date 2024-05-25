package me.edgrrrr.de.commands.money;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.economy.BaltopPlayer;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Map;

public class ListBalances extends DivinityCommand {
    /**
     * Constructor
     *
     * @param main
     */
    public ListBalances(DEPlugin main) {
        super(main, "listbalances", true, Setting.COMMAND_LIST_BALANCES_ENABLE_BOOLEAN);
        this.checkEconomyEnabled = true;
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        // Get page number from args or default to 1
        int pageNumber = (args.length >= 1) ? Converter.getInt(args[0]) : 1;

        Map<Integer, BaltopPlayer[]> orderedBalances = getMain().getEconMan().getOrderedBalances();
        int totalPages = orderedBalances.size();
        int outputNumber = Converter.constrainInt(pageNumber, 1, totalPages);
        double totalAmount = getMain().getEconMan().getTotalEconomySize();

        // Ensure total pages greater than 0
        if (totalPages == 0) {
            getMain().getConsole().send(sender, LangEntry.BALTOP_NothingToDisplay.logLevel, LangEntry.BALTOP_NothingToDisplay.get(getMain()));
            return true;
        }

        Calendar calendar = Calendar.getInstance();
        String am_pm = calendar.get(Calendar.AM_PM) == Calendar.PM ? "PM" : "AM";
        getMain().getConsole().info(sender, "=== (%s/%s)===", outputNumber, totalPages);
        getMain().getConsole().warn(sender, LangEntry.BALTOP_LastOrderedAt.get(getMain(), String.format("%s%s/%s/%s %02d:%02d%s%s", ChatColor.BLUE, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), ChatColor.GREEN, am_pm)));
        getMain().getConsole().warn(sender, LangEntry.BALTOP_ServerTotal.get(getMain(), String.format("%s%s", ChatColor.RED, getMain().getConsole().formatMoney(totalAmount))));
        for (BaltopPlayer player : orderedBalances.get(outputNumber - 1)) {
            getMain().getConsole().info(sender, "%s(%-3s) %s%s%s %s%s", ChatColor.DARK_GRAY, getMain().getEconMan().getBaltopPosition(player.getOfflinePlayer()), ChatColor.WHITE, player.getName(), ChatColor.GREEN, ChatColor.GOLD, getMain().getConsole().formatMoney(player.getBalance()));
        }
        getMain().getConsole().info(sender, "");
        getMain().getConsole().info(sender, "%s%s %s", ChatColor.GRAY, LangEntry.BALTOP_YourPositionIs.get(getMain()), getMain().getEconMan().getBaltopPosition(sender));

        return true;
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        return onPlayerCommand(null, args);
    }
}
