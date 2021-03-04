package EDGRRRR.DCE.Commands.Market;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Materials.MaterialData;
import EDGRRRR.DCE.Materials.MaterialValueResponse;
import EDGRRRR.DCE.Math.Math;
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
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComHandValue))) {
            this.app.getConsoleManager().severe(player, "This command is not enabled.");
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
                this.app.getConsoleManager().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amount < 0) {
            this.app.getConsoleManager().usage(player, "Invalid amount.", this.usage);
        } else {
            ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);
            if (heldItem == null) {
                this.app.getConsoleManager().usage(player, "You are not holding any item.", this.usage);

            } else {
                Material material = heldItem.getType();
                MaterialData materialData = this.app.getMaterialManager().getMaterial(material.name());
                ItemStack[] buyStacks;
                ItemStack[] sellStacks;
                ItemStack[] itemStacks = this.app.getPlayerInventoryManager().getMaterialSlots(player, material);

                if (valueHand) {
                    amount = heldItem.getAmount();
                    buyStacks = this.app.getPlayerInventoryManager().createItemStacks(material, amount);
                    sellStacks = new ItemStack[1];
                    sellStacks[0] = heldItem;
                } else if (valueAll) {
                    amount = this.app.getPlayerInventoryManager().getMaterialCount(itemStacks);
                    sellStacks = itemStacks;
                    buyStacks = this.app.getPlayerInventoryManager().createItemStacks(material, amount);
                } else {
                    sellStacks = this.app.getPlayerInventoryManager().createItemStacks(material, amount);
                    buyStacks = sellStacks;
                }

                MaterialValueResponse buyResponse = this.app.getMaterialManager().getBuyValue(buyStacks);
                MaterialValueResponse sellResponse = this.app.getMaterialManager().getSellValue(sellStacks);

                if (buyResponse.isSuccess()) {
                    this.app.getConsoleManager().info(player, "Buy: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(buyResponse.value));

                } else {
                    this.app.getConsoleManager().usage(player, "Couldn't determine buy price of " + amount + " " + materialData.getCleanName() + " because " + buyResponse.errorMessage, usage);
                }

                if (sellResponse.isSuccess()) {
                    this.app.getConsoleManager().info(player, "Sell: " + amount + " " + materialData.getCleanName() + " costs £" + this.app.getEconomyManager().round(sellResponse.value));
                } else {
                    this.app.getConsoleManager().usage(player, "Couldn't determine sell price of " + amount + " " + materialData.getCleanName() + " because " + sellResponse.errorMessage, usage);
                }
            }
        }
        return true;
    }
}
