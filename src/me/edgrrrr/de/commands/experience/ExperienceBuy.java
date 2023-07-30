package me.edgrrrr.de.commands.experience;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandEnchant;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.response.ValueResponse;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * A command for buying enchants for the item held in a users hand
 */
public class ExperienceBuy extends DivinityCommandEnchant {

    /**
     * Constructor
     *
     * @param app
     */
    public ExperienceBuy(DEPlugin app) {
        super(app, "xpbuy", false, Setting.COMMAND_EXP_BUY_ENABLE_BOOLEAN);
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
        int experience = 1;
        // How to use
        switch (args.length) {
            case 0:
                break;

            case 1:
                if (args[0].equalsIgnoreCase("max")) {
                    experience = this.getMain().getExpMan().getMaxTradableExp();
                } else {
                    experience = Converter.getInt(args[0]);
                }
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        experience = Converter.constrainInt(experience, this.getMain().getExpMan().getMinTradableExp(), this.getMain().getExpMan().getMaxTradableExp());

        // Ensure item valuation was successful
        ValueResponse valueResponse = this.getMain().getExpMan().getBuyValue(experience);
        if (valueResponse.isFailure()) {
            this.getMain().getConsole().logFailedPurchase(sender, experience, "experience", valueResponse.getErrorMessage());
            return true;
        }

        double startingBalance = this.getMain().getEconMan().getBalance(sender);
        EconomyResponse economyResponse = this.getMain().getEconMan().remCash(sender, valueResponse.getValue());

        if (!economyResponse.transactionSuccess()) {
            this.getMain().getConsole().logFailedPurchase(sender, experience, "experience", economyResponse.errorMessage);
            this.getMain().getEconMan().setCash(sender, startingBalance);
        } else {
            this.getMain().getExpMan().addExperience(sender, experience);
            this.getMain().getConsole().logPurchase(sender, experience, economyResponse.amount, "experience");
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
        return false;
    }
}
