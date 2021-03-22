package edgrrrr.dce.commands.enchants;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.enchants.EnchantData;
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

public class EnchantHandBuy implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/ehb <enchant> <level>";

    public EnchantHandBuy(DCEPlugin app) {
        this.app = app;
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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_E_BUY_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        // Ensure market is enabled
        if (!(this.app.getConfig().getBoolean(Setting.MARKET_ENCHANTS_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "The enchant market is not enabled.");
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
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (enchantLevels < 1) {
            DCEPlugin.CONSOLE.warn(player, "Enchant level cannot be less than 1.");
        } else {
            ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);
            if (heldItem == null) {
                DCEPlugin.CONSOLE.warn(player, "You are not holding any item.");
            } else {
                ValueResponse valueResponse = this.app.getEnchantmentManager().getBuyValue(enchantName, enchantLevels);

                if (valueResponse.isFailure()) {
                    DCEPlugin.CONSOLE.logFailedPurchase(player, enchantLevels, valueResponse.value, enchantName, valueResponse.errorMessage);
                } else {
                    EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);

                    if (enchantData == null) {
                        DCEPlugin.CONSOLE.logFailedPurchase(player, enchantLevels, valueResponse.value, enchantName, String.format("enchant name '%s' does not exist", enchantName));
                    } else {
                        double startingBalance = this.app.getEconomyManager().getBalance(player);
                        EconomyResponse economyResponse = this.app.getEconomyManager().remCash(player, valueResponse.value);

                        if (!economyResponse.transactionSuccess()) {
                            DCEPlugin.CONSOLE.logFailedPurchase(player, enchantLevels, valueResponse.value, enchantName, economyResponse.errorMessage);
                            this.app.getEconomyManager().setCash(player, startingBalance);
                        } else {
                            Response response = this.app.getEnchantmentManager().addEnchantToItem(heldItem, enchantData.getEnchantment(), enchantLevels);
                            if (response.isFailure()) {
                                DCEPlugin.CONSOLE.logFailedPurchase(player, enchantLevels, valueResponse.value, enchantName, response.errorMessage);
                                this.app.getEconomyManager().setCash(player, startingBalance);
                            } else {
                                DCEPlugin.CONSOLE.logPurchase(player, enchantLevels, valueResponse.value, enchantName);
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
