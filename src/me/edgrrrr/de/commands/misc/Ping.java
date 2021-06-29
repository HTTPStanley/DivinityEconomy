package me.edgrrrr.de.commands.misc;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

/**
 * A simple ping pong! command
 */
public class Ping extends DivinityCommand {
    /**
     * Constructor
     *
     * @param app
     */
    public Ping(DEPlugin app) {
        super(app, "ping", true, Setting.COMMAND_PING_ENABLE_BOOLEAN);
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
        this.getMain().getConsole().send(sender, CommandResponse.PingResponse.defaultLogLevel, CommandResponse.PingResponse.message);
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
        this.getMain().getConsole().send(CommandResponse.PingResponse.defaultLogLevel, CommandResponse.PingResponse.message);
        return true;
    }
}
