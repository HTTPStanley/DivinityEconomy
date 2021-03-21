package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import edgrrrr.dce.player.PlayerInventoryManager;
import edgrrrr.dce.response.ValueResponse;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HandValue implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/handvalue | /handvalue <amount> | /handvalue max";

    public HandValue(DCEPlugin app) {
        this.app = app;

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_VALUE_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        int amount = 1;
        boolean valueAll = false;
        boolean valueHand = false;
        switch (args.length) {
            case 0:
                valueHand = true;
                break;

            case 1:
                String firstArg = args[0].toLowerCase();
                if (firstArg.equals("max")) {
                    valueAll = true;
                } else {
                    amount = Math.getInt(firstArg);
                }
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amount < 0) {
            DCEPlugin.CONSOLE.usage(player, "Invalid amount.", this.usage);
        } else {
            ItemStack heldItem = PlayerInventoryManager.getHeldItem(player);
            if (heldItem == null) {
                DCEPlugin.CONSOLE.usage(player, "You are not holding any item.", this.usage);

            } else {
                Material material = heldItem.getType();
                MaterialData materialData = this.app.getMaterialManager().getMaterial(material.name());
                ItemStack[] buyStacks;
                ItemStack[] sellStacks;
                ItemStack[] itemStacks = PlayerInventoryManager.getMaterialSlots(player, material);

                if (valueHand) {
                    amount = heldItem.getAmount();
                    buyStacks = PlayerInventoryManager.createItemStacks(material, amount);
                    sellStacks = new ItemStack[1];
                    sellStacks[0] = heldItem;
                } else if (valueAll) {
                    amount = PlayerInventoryManager.getMaterialCount(itemStacks);
                    sellStacks = itemStacks;
                    buyStacks = PlayerInventoryManager.createItemStacks(material, amount);
                } else {
                    sellStacks = PlayerInventoryManager.createItemStacks(material, amount);
                    buyStacks = sellStacks;
                }

                ValueResponse buyResponse = this.app.getMaterialManager().getBuyValue(buyStacks);
                ValueResponse sellResponse = this.app.getMaterialManager().getSellValue(sellStacks);

                if (buyResponse.isSuccess()) {
                    DCEPlugin.CONSOLE.info(player, "Buy: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(buyResponse.value));

                } else {
                    DCEPlugin.CONSOLE.usage(player, "Couldn't determine buy price of " + amount + " " + materialData.getCleanName() + " because " + buyResponse.errorMessage, usage);
                }

                if (sellResponse.isSuccess()) {
                    DCEPlugin.CONSOLE.info(player, "Sell: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(sellResponse.value));
                } else {
                    DCEPlugin.CONSOLE.usage(player, "Couldn't determine sell price of " + amount + " " + materialData.getCleanName() + " because " + sellResponse.errorMessage, usage);
                }
            }
        }
        return true;
    }
}
