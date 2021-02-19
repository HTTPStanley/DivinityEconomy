package EDGRRRR.DCE.Commands;

import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Economy.Materials.MaterialData;

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
    private String usage = "/hs or /hs <amount> or /hs max";

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

        int amountToSell = 0;
        boolean sellAll = false;

        switch (args.length) {
            case 0:
                amountToSell = 1;
                break;

            case 1:
                if (args[1] == "max") {
                    sellAll = true;
                } else {
                    amountToSell = (int) (double) this.app.getEco().getDouble(args[1]);
                }
                break;

            default:
                this.app.getCon().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }

        int slotIdx = player.getInventory().getHeldItemSlot();
        ItemStack iStack = player.getInventory().getItem(slotIdx);

        if (iStack == null) {
            this.app.getCon().usage(player, "You are not holding any item.", this.usage);
        } else {
            int amount = iStack.getAmount();
            Material material = iStack.getType();
            String name = material.name();
            MaterialData mData = this.app.getMat().getMaterial(name);
            HashMap<Integer, ? extends ItemStack> inventory = player.getInventory().all(material);
        }


        return true;
    }
}
