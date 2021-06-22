package edgrrrr.de.commands.mail;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.mail.Mail;
import edgrrrr.de.mail.MailList;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

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
                        this.app.getConsole().usage(sender, CommandResponse.InvalidArguments.message, this.help.getUsages());
                        return true;
                }
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }

        MailList mailList = this.app.getMailManager().getMailList(sender.getUniqueId().toString());
        HashMap<String, Mail> allMail = mailList.getAllMail();
        ArrayList<String> readMail = mailList.getReadMail();
        ArrayList<String> unreadMail = mailList.getUnreadMail();
        ArrayList<String> mailToClear = new ArrayList<>();
        int readMailCleared = 0;
        int unreadMailCleared = 0;

        if (allMail.isEmpty()) {
            this.app.getConsole().warn(sender, "You have no mail to clear.");
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
            this.app.getConsole().info(sender, String.format("Removed %d mail. (%d unread & %d read)", mailToClear.size(), unreadMailCleared, readMailCleared));

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
