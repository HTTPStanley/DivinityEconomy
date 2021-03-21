package edgrrrr.dce.commands.market;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.materials.MaterialData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class HandInfoTC implements TabCompleter {
    private final DCEPlugin app;

    public HandInfoTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_HAND_INFO_ENABLE_BOOLEAN.path()))) {
            return null;
        }

        Player player = (Player) sender;

        String[] strings = new String[0];
        ItemStack heldItem = this.app.getPlayerInventoryManager().getHeldItem(player);
        if (heldItem == null) {
            strings = new String[]{"You are not holding any item."};
        } else {
            MaterialData materialData = this.app.getMaterialManager().getMaterial(heldItem.getType().toString());
            // 1 args
            // return max stack size for the material given
            if (args.length == 1) {
                strings = new String[]{
                        String.valueOf(materialData.getMaterial().getMaxStackSize())
                };
            }
        }

        return Arrays.asList(strings);
    }
}