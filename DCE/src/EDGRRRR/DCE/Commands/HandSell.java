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

/**
 * A simple ping pong! command
 */
public class HandSell implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/hs | /hs <amount> | /hs max";

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
                    amountToSell = (int) this.app.getEco().getDouble(args[0]);
                }
                break;

            default:
                this.app.getCon().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        if (amountToSell < 1) {
            this.app.getCon().usage(player, "Invalid amount.", this.usage);
        } else {
            int slotIdx = player.getInventory().getHeldItemSlot();
            ItemStack iStack = player.getInventory().getItem(slotIdx);

            if (iStack == null) {
                this.app.getCon().usage(player, "You are not holding any item.", this.usage);
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
                if (!mData.getAllowed()) {
                    this.app.getCon().usage(player, "Cannot sell" + mData.getCleanName() + " when it is not allowed to be bought or sold", this.usage);
                    this.app.getCon().warn(player.getName() + " couldn't sell " + mData.getMaterialID() + " when it is not allowed to be bought or sold");
                } else {
                    if (materialCount < amountToSell) {
                        this.app.getCon().usage(player, "Cannot sell " + amountToSell + " " + mData.getCleanName() + " when you only have " + materialCount, this.usage);
                        this.app.getCon().warn(player.getName() + " couldn't sell " + amountToSell + " " + mData.getMaterialID() + " because they only have " + materialCount + " / " + amountToSell);
                    } else {
                        this.app.getMat().removeMaterialsFromPlayer(itemStacks, amountToSell);
                        mData.addQuantity(amountToSell);
                        EconomyResponse response = this.app.getMat().getMaterialPrice(mData, amountToSell, 1.0, false);
                        this.app.getEco().addCash(player, response.balance);
                        double cost = app.getEco().round(response.balance);
                        double balance = app.getEco().round(app.getEco().getBalance(player));

                        this.app.getCon().info(player, "Sold " + amountToSell + " " + mData.getCleanName() + " for £" + cost + ". New Balance: £" + balance);
                        this.app.getCon().info(player.getName() + " sold " + amountToSell + " " + mData.getMaterialID() + " for £" + cost);
                    }
                }
            }
        }
        return true;
    }
}
