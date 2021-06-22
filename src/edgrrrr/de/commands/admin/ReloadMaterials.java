package edgrrrr.de.commands.admin;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import org.bukkit.entity.Player;

/**
 * A command for reloading the materials
 */
public class ReloadMaterials extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ReloadMaterials(DEPlugin app) {
        super(app, "reloadmaterials", true, Setting.COMMAND_RELOAD_MATERIALS_ENABLE_BOOLEAN);
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
        this.app.getMaterialManager().loadMaterials();
        this.app.getMaterialManager().loadAliases();
        this.app.getConsole().info(sender, "Reloaded Materials");
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
