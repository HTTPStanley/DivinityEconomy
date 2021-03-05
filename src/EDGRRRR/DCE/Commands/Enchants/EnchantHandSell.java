package EDGRRRR.DCE.Commands.Enchants;

import EDGRRRR.DCE.Enchants.EnchantData;
import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * A command for selling enchants on held items
 */
public class EnchantHandSell implements CommandExecutor {
    // The main class
    private final DCEPlugin app;
    // The usage for this command
    private final String usage = "/ehs <enchant> <levels> | /ehs <enchant>";

    /**
     * Constructor
     * @param app - The main class
     */
    public EnchantHandSell(DCEPlugin app) {
        this.app = app;
    }

    /**
     * Called everytime the command /eHandSell is called
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        // Ensure sender is a player
        if (!(sender instanceof Player)) {
            return true;
        }

        // Cast to player
        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComEnchantHandSell))) {
            this.app.getConsoleManager().severe(player, "This command is not enabled.");
            return true;
        }

        // The name of the enchant
        // The number of levels to sell
        // If all levels should be sold
        String enchantName;
        int enchantLevels = 1;
        boolean enchantSellAll = false;

        switch (args.length) {
            // If user enters only the command
            // Sell all enchants on item
            case 0:
                enchantName = "*";
                enchantSellAll = true;
                break;

            // If user enters the name
            // sell maximum of enchant given
            case 1:
                enchantName = args[0];
                enchantSellAll = true;
                break;

            // If user enters name and level
            // Sell enchant level times
            case 2:
                enchantName = args[0];
                enchantLevels = Math.getInt(args[1]);
                break;

            // If wrong number of arguments
            default:
                this.app.getConsoleManager().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        // get the item the user is holding.
        // ensure it is not null
        ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);
        if (heldItem == null) {
            this.app.getConsoleManager().usage(player, "You are not holding any item", this.usage);

        } else {

        }

        // Graceful exit :)
        return true;
    }
}
