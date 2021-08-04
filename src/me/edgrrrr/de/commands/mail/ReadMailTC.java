package me.edgrrrr.de.commands.mail;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommandTC;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.mail.MailList;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the read mail command
 */
public class ReadMailTC extends DivinityCommandTC {

    /**
     * Constructor
     *
     * @param app
     */
    public ReadMailTC(DEPlugin app) {
        super(app, "readmail", false, Setting.COMMAND_READ_MAIL_ENABLE_BOOLEAN);
    }

    /**
     * For handling a player calling this command
     *
     * @param sender
     * @param args
     * @return
     */
    @Override
    public List<String> onPlayerTabCompleter(Player sender, String[] args) {
        String[] strings;
        MailList mailList = this.getMain().getMailMan().getMailList((sender).getUniqueId().toString());
        switch (args.length) {
            // 1 arg
            // return list of page numbers
            case 1:
                ArrayList<String> pageNums = new ArrayList<>();
                for (int pageNum : mailList.getPages(10).keySet()) {
                    // +1 since readMail interprets numbers as pgnum + 1
                    pageNums.add(String.valueOf(pageNum + 1));
                }
                strings = pageNums.toArray(new String[0]);
                break;

            // else
            default:
                strings = new String[0];
                break;
        }

        return Arrays.asList(strings);
    }

    /**
     * For the handling of the console calling this command
     *
     * @param args
     * @return
     */
    @Override
    public List<String> onConsoleTabCompleter(String[] args) {
        return null;
    }
}
