package edgrrrr.dce.commands.mail;

import edgrrrr.configapi.Setting;
import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.mail.Mail;
import edgrrrr.dce.mail.MailList;
import edgrrrr.dce.math.Math;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * A command for reading mail
 */
public class ReadMail implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public ReadMail(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("readmail");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_READ_MAIL_ENABLE_BOOLEAN.path))) {
            this.app.getConsole().severe(player, "This command is not enabled.");
            return true;
        }

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
                this.app.getConsole().usage(player, "Invalid number of arguments.", this.help.getUsages());
                return true;
        }
        if (page < 0) {
            this.app.getConsole().usage(player, "Invalid page.", this.help.getUsages());

        } else {

            MailList mailList = this.app.getMailManager().getMailList(player.getUniqueId().toString());

            if (mailList.getAllMail().isEmpty()) {
                this.app.getConsole().warn(player, "You have no mail.");
            } else {
                HashMap<Integer, Mail[]> mailPages = mailList.getPages(pageSize);

                if ((page + 1) > mailPages.size()) {
                    this.app.getConsole().warn(player, "Invalid page. Choose a number up to " + mailPages.size());
                } else {
                    Mail[] mailPage = mailPages.get(page);
                    this.app.getConsole().info(player, "Mail List (" + (page + 1) + "/" + mailPages.size() + ")");
                    int idx = 1;
                    for (Mail mail : mailPage) {
                        if (mail == null) {
                            break;
                        }
                        this.app.getConsole().info(player, idx + ": " + mail.getMessage());
                        mail.setRead(true);
                        idx += 1;
                    }
                }
            }
        }
        return true;
    }
}
