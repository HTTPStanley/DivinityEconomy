package edgrrrr.dce.commands.enchants;

import edgrrrr.dce.config.Setting;
import edgrrrr.dce.DCEPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnchantBuy implements CommandExecutor {
    private DCEPlugin app;
    private String usage;

    public EnchantBuy(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_E_BUY_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

        DCEPlugin.CONSOLE.info(player, "Pong!");
        return true;
    }
}
