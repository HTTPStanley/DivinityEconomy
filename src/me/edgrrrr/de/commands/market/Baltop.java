package me.edgrrrr.de.commands.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.math.Math;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class Baltop extends DivinityCommand {
    /**
     * Constructor
     *
     * @param main
     */
    public Baltop(DEPlugin main) {
        super(main, "baltop", true, Setting.COMMAND_BALTOP_ENABLE_BOOLEAN);
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
        int pageNumber = 0;

        // Args 0 - return baltop
        // Args 1 - return page
        switch (args.length) {
            case 0:
                break;

            case 1:
                pageNumber = Math.getInt(args[0]) - 1;
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        int totalPages = this.getMain().getEconMan().getOrderedBalances().keySet().size();
        Map<Integer, Map.Entry<UUID, Double>[]> orderedBalances = this.getMain().getEconMan().getOrderedBalances();
        double totalAmount = this.getMain().getEconMan().getTotalEconomySize();

        // Ensure page limit inside
        if (pageNumber >  totalPages - 1 || pageNumber < 0) {
            this.getMain().getConsole().send(sender, CommandResponse.InvalidAmountGiven.defaultLogLevel, CommandResponse.InvalidAmountGiven.message);
            return true;
        }

        Calendar calendar = Calendar.getInstance();
        String am_pm;
        if (calendar.get(Calendar.AM_PM) == 1) am_pm = "PM";
        else am_pm = "AM";
        // Print
        this.getMain().getConsole().info(sender, "===Baltop (%s/%s)===", pageNumber + 1, totalPages);
        this.getMain().getConsole().warn(sender, "Last-Ordered at %s%s/%s/%s %02d:%02d%s%s", ChatColor.BLUE, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), ChatColor.GREEN, am_pm);
        this.getMain().getConsole().warn(sender, "Server Total: %s%s", ChatColor.RED, this.getMain().getConsole().formatMoney(totalAmount));
        for (Map.Entry<UUID, Double> entry : orderedBalances.get(pageNumber)) {
            String name;
            OfflinePlayer player = this.getMain().getPlayMan().getOfflinePlayer(entry.getKey(), true);
            Player oPlayer = player.getPlayer();
            if (oPlayer != null) name = oPlayer.getDisplayName();
            else name = player.getName();

            this.getMain().getConsole().info(sender, " -%s%s%s: %s%s", ChatColor.WHITE, name, ChatColor.GREEN, ChatColor.GOLD, this.getMain().getConsole().formatMoney(entry.getValue()));
        }

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
