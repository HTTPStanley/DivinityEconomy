package edgrrrr.dce.commands.enchants;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.enchants.EnchantData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.MultiValueResponse;
import edgrrrr.dce.response.ValueResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantHandValue implements CommandExecutor {
    private DCEPlugin app;
    private String usage = "/ehv | /ehv <enchant> | /ehv <enchant> <levels>";

    public EnchantHandValue(DCEPlugin app) {
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
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_E_HAND_VALUE_ENABLE_BOOLEAN.path()))) {
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
        ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);
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
                    MultiValueResponse multiValueResponse1 = this.app.getEnchantmentManager().getSellValue(heldItem);
                    MultiValueResponse multiValueResponse2 = this.app.getEnchantmentManager().getBuyValue(heldItem);
                    if (multiValueResponse1.isFailure()) {
                        DCEPlugin.CONSOLE.warn(player, String.format("Couldn't determine buy value of &d Enchants(%s) because %s", multiValueResponse2.getTotalQuantity(), multiValueResponse2.toString(), multiValueResponse2.errorMessage));
                    } else {
                        DCEPlugin.CONSOLE.info(player, String.format("Buy: %d Enchants(%s) costs £%,.2f", multiValueResponse2.getTotalQuantity(), multiValueResponse2.toString(), multiValueResponse2.getTotalValue()));
                    }
                    if (multiValueResponse1.isFailure()) {
                        DCEPlugin.CONSOLE.warn(player, String.format("Couldn't determine sell value of &d Enchants(%s) because %s", multiValueResponse1.getTotalQuantity(), multiValueResponse1.toString(), multiValueResponse1.errorMessage));
                    } else {
                        DCEPlugin.CONSOLE.info(player, String.format("Sell: %d Enchants(%s) costs £%,.2f", multiValueResponse1.getTotalQuantity(), multiValueResponse1.toString(), multiValueResponse1.getTotalValue()));
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
                        ValueResponse valueResponse1 = this.app.getEnchantmentManager().getSellValue(heldItem, enchantName, enchantLevels);
                        ValueResponse valueResponse2 = this.app.getEnchantmentManager().getBuyValue(enchantName, enchantLevels);
                        if (valueResponse2.isFailure()) {
                            DCEPlugin.CONSOLE.warn(player, String.format("Couldn't determine buy value of &d Enchants(%s) because %s", enchantLevels, enchantName, valueResponse2.errorMessage));
                        } else {
                            DCEPlugin.CONSOLE.info(player, String.format("Buy: %d Enchants(%s) costs £%,.2f", enchantLevels, enchantName, valueResponse2.value));
                        }
                        if (valueResponse1.isFailure()) {
                            DCEPlugin.CONSOLE.warn(player, String.format("Couldn't determine sell value of &d Enchants(%s) because %s", enchantLevels, enchantName, valueResponse1.errorMessage));
                        } else {
                            DCEPlugin.CONSOLE.info(player, String.format("Sell: %d Enchants(%s) costs £%,.2f", enchantLevels, enchantName, valueResponse1.value));
                        }
                    }
                }
            }
        }

        // Graceful exit :)
        return true;
    }

}
