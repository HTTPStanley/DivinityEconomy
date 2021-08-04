package me.edgrrrr.de.commands.mail;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.mail.Mail;
import me.edgrrrr.de.mail.MailList;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * A command for clearing mail
 */
public class ClearMail extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ClearMail(DEPlugin app) {
        super(app, "clearmail", false, Setting.COMMAND_CLEAR_MAIL_ENABLE_BOOLEAN);
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
        boolean clearRead = false;
        boolean clearUnread = false;
        switch (args.length) {
            case 1:
                String arg = args[0].toLowerCase();
                switch (arg) {
                    case "all":
                        clearRead = true;
                        clearUnread = true;
                        break;

                    case "read":
                        clearRead = true;
                        break;

                    case "unread":
                        clearUnread = true;
                        break;

                    default:
                        this.getMain().getConsole().usage(sender, CommandResponse.InvalidArguments.message, this.help.getUsages());
                        return true;
                }
                break;

            default:
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        MailList mailList = this.getMain().getMailMan().getMailList(sender.getUniqueId().toString());
        Map<String, Mail> allMail = mailList.getAllMail();
        ArrayList<String> readMail = mailList.getReadMail();
        ArrayList<String> unreadMail = mailList.getUnreadMail();
        ArrayList<String> mailToClear = new ArrayList<>();
        int readMailCleared = 0;
        int unreadMailCleared = 0;

        if (allMail.isEmpty()) {
            this.getMain().getConsole().warn(sender, "You have no mail to clear.");
        } else {
            if (clearRead) {
                mailToClear.addAll(readMail);
                readMailCleared = readMail.size();
            }
            if (clearUnread) {
                mailToClear.addAll(unreadMail);
                unreadMailCleared = unreadMail.size();
            }

            for (String mailID : mailToClear) {
                mailList.removeMail(mailID);
            }
            this.getMain().getConsole().info(sender, "Removed %d mail. (%d unread & %d read)", mailToClear.size(), unreadMailCleared, readMailCleared);

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
