package edgrrrr.dce.commands.mail;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
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
    private final String usage = "/readMail | /readMail <page>";

    public ReadMail(DCEPlugin app) {
        this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_READ_MAIL_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
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
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }
        if (page < 0) {
            DCEPlugin.CONSOLE.usage(player, "Invalid page.", this.usage);

        } else {

            MailList mailList = this.app.getMailManager().getMailList(player.getUniqueId().toString());

            if (mailList.getAllMail().isEmpty()) {
                DCEPlugin.CONSOLE.warn(player, "You have no mail.");
            } else {
                HashMap<Integer, Mail[]> mailPages = mailList.getPages(pageSize);

                if ((page + 1) > mailPages.size()) {
                    DCEPlugin.CONSOLE.warn(player, "Invalid page. Choose a number up to " + mailPages.size());
                } else {
                    Mail[] mailPage = mailPages.get(page);
                    DCEPlugin.CONSOLE.info(player, "Mail List (" + (page + 1) + "/" + mailPages.size() + ")");
                    int idx = 1;
                    for (Mail mail : mailPage) {
                        if (mail == null) {
                            break;
                        }
                        DCEPlugin.CONSOLE.info(player, idx + ": " + mail.getMessage());
                        mail.setRead(true);
                        idx += 1;
                    }
                }
            }
        }
        return true;
    }
}
