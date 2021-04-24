package edgrrrr.dce.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommand;
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
    public SaveMaterials(DCEPlugin app) {
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
