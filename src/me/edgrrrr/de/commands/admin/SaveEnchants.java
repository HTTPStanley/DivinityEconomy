package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

/**
 * A command for saving the materials
 */
public class SaveEnchants extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public SaveEnchants(DEPlugin app) {
        super(app, "saveenchants", true, Setting.COMMAND_SAVE_ENCHANTS_ENABLE_BOOLEAN);
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
        this.app.getEnchantmentManager().saveEnchants();
        this.app.getConsole().info(sender, "Saved Enchants");
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
