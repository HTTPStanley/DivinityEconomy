package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

public class Reload extends DivinityCommand {
    /**
     * Constructor
     *
     * @param main
     */
    public Reload(DEPlugin main) {
        super(main, "reload", true, Setting.COMMAND_RELOAD_ENABLE_BOOLEAN);
    }

    /**
     * ###To be overridden by the actual command
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        return this.onConsoleCommand(args);
    }

    /**
     * ###To be overridden by the actual command
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public boolean onConsoleCommand(String[] args) {
        this.getMain().getConsole().info("This command currently does nothing. Thanks for trying it though.");
        return true;
    }
}
