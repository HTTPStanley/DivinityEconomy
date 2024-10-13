package org.divinitycraft.divinityeconomy.commands.mail;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.commands.DivinityCommand;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.mail.Mail;
import org.divinitycraft.divinityeconomy.mail.MailList;
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
        // Check mail is enabled
        if (!getMain().getMailMan().isMailEnabled())  {
            returnCommandDisabled(sender);
            return true;
        }


        boolean clearRead = false;
        boolean clearUnread = false;

        switch (args.length) {
            case 1:
                String arg = args[0].toLowerCase();
                if (LangEntry.W_all.is(getMain(), arg)) {
                    clearRead = true;
                    clearUnread = true;
                } else if (LangEntry.W_read.is(getMain(), arg)) {
                    clearRead = true;
                } else if (LangEntry.W_unread.is(getMain(), arg)) {
                    clearUnread = true;
                } else {
                    getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidArguments.get(getMain()), this.help.getUsages());
                    return true;
                }
                break;
            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }

        MailList mailList = getMain().getMailMan().getMailList(sender.getUniqueId().toString());
        Map<String, Mail> allMail = mailList.getAllMail();
        ArrayList<String> readMail = mailList.getReadMail();
        ArrayList<String> unreadMail = mailList.getUnreadMail();
        ArrayList<String> mailToClear = new ArrayList<>();
        int readMailCleared = 0;
        int unreadMailCleared = 0;

        if (allMail.isEmpty()) {
            getMain().getConsole().warn(sender, LangEntry.MAIL_NothingToClear.get(getMain()));
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
            getMain().getConsole().info(sender, LangEntry.MAIL_Removed.get(getMain()), mailToClear.size(), unreadMailCleared, readMailCleared);

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
