package edgrrrr.dce.commands.enchants;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.Response;
import edgrrrr.dce.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command for buying enchants for the item held in a users hand
 */
public class EnchantHandBuy implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public EnchantHandBuy(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("ebuy");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure sender is a player
        if (!(sender instanceof Player)) {
            return true;
        }

        // Cast to player
        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_E_BUY_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_ENCHANTS_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "The enchant market is not enabled.");
            return true;
        }

        String enchantName = "";
        int enchantLevels = 0;
        // How to use
        switch (args.length){
            case 2:
                enchantName = args[0];
                enchantLevels = Math.getInt(args[1]);
                break;

            default:
                this.app.getConsole().usage(player, "Invalid number of arguments.", this.help.getUsages());
                return true;
        }

        if (enchantLevels < 1) {
            this.app.getConsole().warn(player, "Enchant level cannot be less than 1.");
        } else {
            ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);
            if (heldItem == null) {
                this.app.getConsole().warn(player, "You are not holding any item.");
            } else {
                ValueResponse valueResponse = this.app.getEnchantmentManager().getBuyValue(enchantName, enchantLevels);

                if (valueResponse.isFailure()) {
                    this.app.getConsole().logFailedPurchase(player, enchantLevels, enchantName, valueResponse.errorMessage);
                } else {
                    EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);

                    if (enchantData == null) {
                        this.app.getConsole().logFailedPurchase(player, enchantLevels, enchantName, String.format("enchant name '%s' does not exist", enchantName));

                    } else {
                        double startingBalance = this.app.getEconomyManager().getBalance(player);
                        EconomyResponse economyResponse = this.app.getEconomyManager().remCash(player, valueResponse.value);

                        if (!economyResponse.transactionSuccess()) {
                            this.app.getConsole().logFailedPurchase(player, enchantLevels, enchantName, economyResponse.errorMessage);
                            this.app.getEconomyManager().setCash(player, startingBalance);
                        } else {
                            Response response = this.app.getEnchantmentManager().addEnchantToItem(heldItem, enchantData.getEnchantment(), enchantLevels);
                            if (response.isFailure()) {
                                this.app.getConsole().logFailedPurchase(player, enchantLevels, enchantName, response.errorMessage);
                                this.app.getEconomyManager().setCash(player, startingBalance);
                            } else {
                                this.app.getConsole().logPurchase(player, enchantLevels, valueResponse.value, enchantName);
                                enchantData.remLevelQuantity(enchantLevels);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
