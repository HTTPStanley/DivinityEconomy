package me.edgrrrr.de.commands.mail;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.commands.DivinityCommand;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.mail.Mail;
import me.edgrrrr.de.mail.MailList;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.entity.Player;

import java.util.Map;

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
        // Check mail is enabled
        if (!getMain().getMailMan().isMailEnabled())  {
            returnCommandDisabled(sender);
            return true;
        }


        int page;
        int pageSize = 10;
        switch (args.length) {
            case 0:
                page = 0;
                break;

            case 1:
                page = Converter.getInt(args[0]) - 1;
                break;

            default:
                getMain().getConsole().usage(sender, LangEntry.GENERIC_InvalidNumberOfArguments.get(getMain()), this.help.getUsages());
                return true;
        }
        if (page < 0) {
            getMain().getConsole().usage(sender, LangEntry.MAIL_InvalidPage.get(getMain()), this.help.getUsages());

        } else {

            MailList mailList = getMain().getMailMan().getMailList(sender.getUniqueId().toString());

            if (mailList.getAllMail().isEmpty()) {
                getMain().getConsole().warn(sender, LangEntry.MAIL_YouHaveNoMail.get(getMain()));
            } else {
                Map<Integer, Mail[]> mailPages = mailList.getPages(pageSize);

                if ((page + 1) > mailPages.size()) {
                    getMain().getConsole().warn(sender, LangEntry.MAIL_InvalidPageChoose.get(getMain()), mailPages.size());
                } else {
                    Mail[] mailPage = mailPages.get(page);
                    getMain().getConsole().info(sender, LangEntry.MAIL_List.get(getMain()), (page + 1), mailPages.size());
                    int idx = 1;
                    for (Mail mail : mailPage) {
                        if (mail == null) {
                            break;
                        }
                        getMain().getConsole().info(sender, "%s: %s", idx, mail.getMessage());
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
