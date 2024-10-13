package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.entity.Player;
import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommandMaterials;
import org.divinitycraft.divinityeconomy.gui.GuiListener;
import org.divinitycraft.divinityeconomy.config.Setting;

/**
 * A command for buying items from the market using a GUI
 */
public class BuyGui extends DivinityCommandMaterials {
    private GuiListener guiListener;

    /**
     * Constructor
     *
     * @param app
     */
    public BuyGui(DEPlugin app) {
        super(app, "buygui", false, Setting.COMMAND_BUYGUI_ITEM_ENABLE_BOOLEAN);
        this.guiListener = new GuiListener(getMain());
        this.checkItemMarketEnabled = true;
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
        if (args.length == 0) {
            GuiListener guiListener = new GuiListener(getMain());
            guiListener.openStorageGUI(sender);
        }
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
        return false;
    }
}
