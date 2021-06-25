package edgrrrr.de.commands.admin;

import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

/**
 * A command for saving the materials
 */
public class SaveMaterials extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public SaveMaterials(DEPlugin app) {
        super(app, "savematerials", true, Setting.COMMAND_SAVE_MATERIALS_ENABLE_BOOLEAN);
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
        this.app.getMaterialManager().saveMaterials();
        this.app.getConsole().info(sender, "Saved Materials");
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
