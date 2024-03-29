package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
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
        this.getMain().getMatMan().saveItems();
        this.getMain().getPotMan().saveItems();
        this.getMain().getEntMan().saveItems();
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
