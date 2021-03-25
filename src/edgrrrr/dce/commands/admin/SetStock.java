package edgrrrr.dce.commands.admin;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.materials.MaterialData;
import edgrrrr.dce.math.Math;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command for setting the stock of a material
 */
public class SetStock implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public SetStock(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("setstock");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player;
        if (commandSender instanceof Player) {
            player = (Player) commandSender;
        } else {
            player = null;
        }

        MaterialData materialData = null;
        int stock = -1;
        switch (strings.length) {
            case 2:
                materialData = this.app.getMaterialManager().getMaterial(strings[0]);
                stock = Math.getInt(strings[1]);
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Not enough arguments.", this.help);
                break;
        }

        if (materialData == null) {
            DCEPlugin.CONSOLE.warn(player, String.format("Unrecognized material '%s'", strings[0]));
        } else {
            if (stock < 0) {
                DCEPlugin.CONSOLE.warn(player, "Stock level must be equal to or above 0.");
            } else {
                int previousStock = materialData.getQuantity();
                double previousValue = materialData.getUserPrice();
                materialData.setQuantity(stock);
                DCEPlugin.CONSOLE.info(player, String.format("Changed stock level from %d(£%,.2f) to %d(£%,.2f).", previousStock, previousValue, stock, materialData.getUserPrice()));
            }
        }

        return true;
    }
}
