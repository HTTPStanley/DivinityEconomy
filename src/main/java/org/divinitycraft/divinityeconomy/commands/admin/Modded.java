package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.bukkit.entity.Player;

public class Modded extends DivinityCommand {

    /**
     * Constructor
     *
     * @param main
     */
    public Modded(DEPlugin main) {
        super(main, "modded", true, Setting.COMMAND_MODDED_ENABLE_BOOLEAN);
    }

    /**
     * Rescans the server for modded materials and reports the result to the sender
     *
     * @param sender - The player, or null for the console
     * @return
     */
    private boolean run(Player sender) {
        int reloaded;
        try {
            reloaded = getMain().getMatMan().reloadModdedItems();
        } catch (Exception e) {
            getMain().getConsole().warn(sender, LangEntry.MODDED_ReloadFailed.get(getMain()), e.toString());
            return false;
        }

        if (reloaded > 0) {
            getMain().getConsole().info(sender, LangEntry.MODDED_Reloaded.get(getMain()), reloaded);
        } else {
            getMain().getConsole().info(sender, LangEntry.MODDED_NoneReloaded.get(getMain()));
        }
        return true;
    }

    @Override
    public boolean onPlayerCommand(Player sender, String[] args) {
        return this.run(sender);
    }

    @Override
    public boolean onConsoleCommand(String[] args) {
        return this.run(null);
    }
}
