package edgrrrr.de.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import org.bukkit.entity.Player;

/**
 * A command for reloading the enchants
 */
public class ReloadEnchants extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ReloadEnchants(DEPlugin app) {
        super(app, "reloadenchants", true, Setting.COMMAND_RELOAD_ENCHANTS_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        this.app.getEnchantmentManager().loadEnchants();
        this.app.getConsole().info(sender, "Reloaded Enchants");
        return true;
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        return this.onPlayerCommand(null, args);
    }
}
