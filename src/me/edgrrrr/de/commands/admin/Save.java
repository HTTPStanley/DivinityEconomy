package me.edgrrrr.de.commands.admin;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.entity.Player;

public class Save extends DivinityCommand {
    /**
     * Constructor
     *
     * @param main
     */
    public Save(DEPlugin main) {
        super(main, "save", true, Setting.COMMAND_SAVE_ENABLE_BOOLEAN);
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
        if (args.length == 0) {
            // Get the first argument
            String arg = args[0].toLowerCase();

            // If the argument is "config"
            if (LangEntry.W_config.is(getMain(), arg)) {
                getMain().saveConfig();
                getMain().getConsole().info(sender, LangEntry.SAVE_Config.get(getMain()));
                return true;
            }


            // If the argument is "materials"
            else if (LangEntry.W_materials.is(getMain(), arg)) {
                getMain().getMatMan().loadItems();
                getMain().getConsole().info(sender, LangEntry.SAVE_Materials.get(getMain()));
                return true;
            }

            // If the argument is "enchants"
            else if (LangEntry.W_enchants.is(getMain(), arg)) {
                getMain().getEnchMan().loadItems();
                getMain().getConsole().info(sender, LangEntry.SAVE_Enchants.get(getMain()));
                return true;
            }


            // If the argument is "potions"
            else if (LangEntry.W_potions.is(getMain(), arg)) {
                getMain().getPotMan().loadItems();
                getMain().getConsole().info(sender, LangEntry.SAVE_Potions.get(getMain()));
                return true;
            }


            // If the argument is "entities"
            else if (LangEntry.W_entities.is(getMain(), arg)) {
                getMain().getEntMan().loadItems();
                getMain().getConsole().info(sender, LangEntry.SAVE_Entities.get(getMain()));
                return true;
            }


            // If the argument is "experience"
            else if (LangEntry.W_experience.is(getMain(), arg)) {
                getMain().getExpMan().loadItems();
                getMain().getConsole().info(sender, LangEntry.SAVE_Experience.get(getMain()));
                return true;
            }
        }

        getMain().getConsole().usage(sender, LangEntry.SAVE_TypeRequired.get(getMain()), this.help.getUsages());
        return false;
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
        return this.onPlayerCommand(null, args);
    }
}
