package edgrrrr.dce.commands.mail;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.commands.DivinityCommandTC;
import edgrrrr.dce.mail.MailList;
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
    public ReadMailTC(DCEPlugin app) {
        super(app, false, Setting.COMMAND_READ_MAIL_ENABLE_BOOLEAN);
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
        MailList mailList = this.app.getMailManager().getMailList(((Player) sender).getUniqueId().toString());
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
