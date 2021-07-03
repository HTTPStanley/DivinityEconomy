package me.edgrrrr.de.commands.mail;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.mail.Mail;
import me.edgrrrr.de.mail.MailList;
import me.edgrrrr.de.math.Math;
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
                this.getMain().getConsole().usage(sender, CommandResponse.InvalidNumberOfArguments.message, this.help.getUsages());
                return true;
        }
        if (page < 0) {
            this.getMain().getConsole().usage(sender, "Invalid page.", this.help.getUsages());

        } else {

            MailList mailList = this.getMain().getMailManager().getMailList(sender.getUniqueId().toString());

            if (mailList.getAllMail().isEmpty()) {
                this.getMain().getConsole().warn(sender, "You have no mail.");
            } else {
                HashMap<Integer, Mail[]> mailPages = mailList.getPages(pageSize);

                if ((page + 1) > mailPages.size()) {
                    this.getMain().getConsole().warn(sender, "Invalid page. Choose a number up to %s", mailPages.size());
                } else {
                    Mail[] mailPage = mailPages.get(page);
                    this.getMain().getConsole().info(sender, "Mail List (%s / %s)", (page + 1), mailPages.size());
                    int idx = 1;
                    for (Mail mail : mailPage) {
                        if (mail == null) {
                            break;
                        }
                        this.getMain().getConsole().info(sender, "%s: %s", idx, mail.getMessage());
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
