package me.edgrrrr.de.commands.misc;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
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
        getMain().getConsole().send(sender, LangEntry.PING_PingResponse.logLevel, LangEntry.PING_PingResponse.get(getMain()));
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
        getMain().getConsole().send(LangEntry.PING_PingResponse.logLevel, LangEntry.PING_PingResponse.get(getMain()));
        return true;
    }
}
