package edgrrrr.dce.commands.enchants;

import edgrrrr.dce.config.Setting;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.response.MultiValueResponse;
import edgrrrr.dce.response.ValueResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_E_HAND_SELL_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        // The name of the enchant
        // The number of levels to sell
        // If all levels should be sold
        String enchantName= "";
        int enchantLevels = 1;
        boolean sellAllLevels = false;
        boolean sellAllEnchants = false;

        switch (args.length) {
            // If user enters only the command
            // Sell all enchants on item
            case 0:
                sellAllEnchants = true;
                sellAllLevels = true;
                break;

            // If user enters the name
            // sell maximum of enchant given
            case 1:
                enchantName = args[0];
                sellAllLevels = true;
                break;

            // If user enters name and level
            // Sell enchant level times
            case 2:
                enchantName = args[0];
                enchantLevels = Math.getInt(args[1]);
                break;

            // If wrong number of arguments
            default:
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        // get the item the user is holding.
        // ensure it is not null
        ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);
        if (heldItem == null) {
            DCEPlugin.CONSOLE.usage(player, "You are not holding any item", this.usage);

        } else {
            // Ensure item is enchanted
            if (!this.app.getEnchantmentManager().isEnchanted(heldItem)){
                DCEPlugin.CONSOLE.usage(player, "The item you are holding is not enchanted", this.usage);

            } else {
                // If sell all enchants is true
                // Then use MultiValueResponse and use getSellValue of entire item
                // Then add quantity of each enchant / remove enchant from item
                // Then add cash
                if (sellAllEnchants) {
                    MultiValueResponse multiValueResponse = this.app.getEnchantmentManager().getSellValue(heldItem);
                    if (multiValueResponse.isFailure()) {
                        DCEPlugin.CONSOLE.logFailedSale(player, multiValueResponse.getTotalQuantity(), multiValueResponse.getTotalValue(), multiValueResponse.toString("Enchants: "), multiValueResponse.errorMessage);
                    } else {
                        for (String enchantID : multiValueResponse.getItemIds()) {
                            EnchantData enchantmentData = this.app.getEnchantmentManager().getEnchant(enchantID);
                            enchantmentData.addLevelQuantity(multiValueResponse.quantities.get(enchantID));
                            this.app.getEnchantmentManager().removeEnchantLevelsFromItem(heldItem, enchantmentData.getEnchantment(), multiValueResponse.quantities.get(enchantID));
                        }
                        this.app.getEconomyManager().addCash(player, multiValueResponse.getTotalValue());
                        DCEPlugin.CONSOLE.logSale(player, multiValueResponse.getTotalQuantity(), multiValueResponse.getTotalValue(), String.format("enchants(%s)", multiValueResponse.toString()));
                    }
                } else {
                    // If only handling one enchant
                    // Ensure enchant exists
                    EnchantData enchantData = this.app.getEnchantmentManager().getEnchant(enchantName);
                    if (enchantData == null) {
                        DCEPlugin.CONSOLE.usage(player, String.format("Unknown enchant name %s", enchantName), this.usage);
                    } else {
                        // Update enchantLevels to the max if sellAllEnchants is true
                        if (sellAllLevels) {
                            enchantLevels = heldItem.getEnchantmentLevel(enchantData.getEnchantment());
                        }


                        // Get value
                        // Remove enchants, add quantity and add cash
                        ValueResponse valueResponse = this.app.getEnchantmentManager().getSellValue(heldItem, enchantName, enchantLevels);
                        if (valueResponse.isFailure()) {
                            DCEPlugin.CONSOLE.logFailedSale(player, enchantLevels, valueResponse.value, enchantName, valueResponse.errorMessage);

                        } else {
                            this.app.getEnchantmentManager().removeEnchantLevelsFromItem(heldItem, enchantData.getEnchantment(), enchantLevels);
                            enchantData.addLevelQuantity(enchantLevels);
                            this.app.getEconomyManager().addCash(player, valueResponse.value);
                            DCEPlugin.CONSOLE.logSale(player, enchantLevels, valueResponse.value, enchantName);
                        }
                    }
                }
            }
        }

        // Graceful exit :)
        return true;
    }
}
