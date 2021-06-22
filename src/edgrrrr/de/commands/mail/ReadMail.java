package edgrrrr.de.commands.mail;

import edgrrrr.configapi.Setting;
import edgrrrr.de.DEPlugin;
import edgrrrr.de.commands.DivinityCommand;
import edgrrrr.de.mail.Mail;
import edgrrrr.de.mail.MailList;
import edgrrrr.de.math.Math;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * A command for reading mail
 */
public class ReadMail extends DivinityCommand {

    /**
     * Constructor
     *
     * @param app
     */
    public ReadMail(DEPlugin app) {
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
    public boolean onPlayerCommand(Player sender, String[] args) {
        int page;
        int pageSize = 10;
        switch (args.length) {
            case 0:
                page = 0;
                break;

            case 1:
                page = Math.getInt(args[0]) - 1;
                break;

            default:
                this.app.getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }
        if (page < 0) {
            this.app.getConsole().usage(sender, "Invalid page.", this.help.getUsages());

        } else {

            MailList mailList = this.app.getMailManager().getMailList(sender.getUniqueId().toString());

            if (mailList.getAllMail().isEmpty()) {
                this.app.getConsole().warn(sender, "You have no mail.");
            } else {
                HashMap<Integer, Mail[]> mailPages = mailList.getPages(pageSize);

                if ((page + 1) > mailPages.size()) {
                    this.app.getConsole().warn(sender, "Invalid page. Choose a number up to " + mailPages.size());
                } else {
                    Mail[] mailPage = mailPages.get(page);
                    this.app.getConsole().info(sender, "Mail List (" + (page + 1) + "/" + mailPages.size() + ")");
                    int idx = 1;
                    for (Mail mail : mailPage) {
                        if (mail == null) {
                            break;
                        }
                        this.app.getConsole().info(sender, idx + ": " + mail.getMessage());
                        mail.setRead(true);
                        idx += 1;
                    }
                }
            }
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
