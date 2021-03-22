package edgrrrr.dce.commands.mail;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.mail.MailList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A tab completer for the read mail command
 */
public class ReadMailTC implements TabCompleter {
    private final DCEPlugin app;

    public ReadMailTC(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        // Ensure player
        if (!(sender instanceof Player) || !(this.app.getConfig().getBoolean(Setting.COMMAND_READ_MAIL_ENABLE_BOOLEAN.path()))) {
            return null;
        }

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
}
