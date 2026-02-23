package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.bukkit.entity.Player;

public class Modded extends DivinityCommand {

    public Modded(DEPlugin main) {
        super(main, "modded", true, Setting.COMMAND_RELOAD_ENABLE_BOOLEAN);
    }

    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        int reloaded = getMain().getMatMan().reloadModdedItems();
        if (reloaded > 0) {
            getMain().getConsole().info(sender, "Reloaded %d modded materials", reloaded);
        } else {
            getMain().getConsole().info(sender, "No modded materials were reloaded");
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(String[] args) {
        int reloaded = getMain().getMatMan().reloadModdedItems();
        if (reloaded > 0) {
            getMain().getConsole().info("Reloaded %d modded materials", reloaded);
        } else {
            getMain().getConsole().info("No modded materials were reloaded");
        }
        return true;
    }
}
