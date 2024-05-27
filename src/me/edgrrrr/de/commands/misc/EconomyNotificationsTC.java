package me.edgrrrr.de.commands.misc;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple ping pong! command
 */
public class EconomyNotificationsTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public EconomyNotificationsTC(DEPlugin app) {
        super(app, "economynotifications", true, Setting.COMMAND_ECONOMY_NOTIFICATIONS_ENABLE_BOOLEAN);
    }


    @Override
    public List<String> onPlayerTabCompleter(Player sender, String[] args) {
        List<String> list = new ArrayList<>();

        switch (args.length) {
            case 1:
                list.add("true");
                list.add("false");
                break;
        }

        return list;
    }

    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        List<String> list = new ArrayList<>();

        switch (args.length) {
            case 1:
                list.addAll(List.of(this.getMain().getPlayMan().getPlayerNames(args[0])));
                break;

            case 2:
                list.add("true");
                list.add("false");
                break;
        }

        return list;
    }
}
