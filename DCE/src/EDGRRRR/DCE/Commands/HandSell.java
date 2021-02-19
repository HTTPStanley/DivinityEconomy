package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Economy.Materials.MaterialData;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * A simple ping pong! command
 */
public class HandSell implements CommandExecutor {
    private DCEPlugin app;
    private String usage = "/hs | /hs <amount> | /hs max";

    public HandSell(DCEPlugin app) {
        this.app = app;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConf().strComHandSell))) {
            this.app.getCon().severe(player, "This command is not enabled.");
            return true;
        }

        int amountToSell = 1;
        boolean sellAll = false;
        boolean sellHand = false;

        switch (args.length) {
            case 0:
                sellHand = true;
                break;

            case 1:
                if (args[0].equals("max")) {
                    sellAll = true;
                } else {
                    amountToSell = (int) (double) this.app.getEco().getDouble(args[0]);
                }
                break;

            default:
                this.app.getCon().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amountToSell < 1) {
            this.app.getCon().usage(player, "Cannot sell less than 1 item", this.usage);
            return true;
        }

        int slotIdx = player.getInventory().getHeldItemSlot();
        ItemStack iStack = player.getInventory().getItem(slotIdx);

        if (iStack == null) {
            this.app.getCon().usage(player, "You are not holding any item.", this.usage);
            return true;
        } else {
            Material material = iStack.getType();
            String materialName = material.name();
            MaterialData mData = this.app.getMat().getMaterial(materialName);
            ItemStack[] itemStacks = this.app.getMat().getMaterialSlots(player, material);
            int materialCount = this.app.getMat().getMaterialCount(itemStacks);

            if (sellAll) {
                amountToSell = materialCount;
            }

            if (sellHand) {
                amountToSell = iStack.getAmount();
            }

            if (materialCount < amountToSell) {
                this.app.getCon().usage(player, "Cannot sell " + amountToSell + " " + mData.getCleanName() + " when you only have " + materialCount, this.usage);
                return true;
            }

            this.app.getMat().removeMaterialsFromPlayer(itemStacks, amountToSell);
            mData.addQuantity(amountToSell);
            EconomyResponse response = this.app.getMat().getMaterialPrice(mData, amountToSell, 1.0, false);
            this.app.getEco().addCash(player, response.balance);

            this.app.getCon().info(player, "Sold " + amountToSell + " " + mData.getCleanName() + " for £" + app.getEco().round(response.balance) + ". New Balance: £" + app.getEco().round(app.getEco().getBalance(player)));
        }


        return true;
    }
}
