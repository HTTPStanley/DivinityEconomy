package EDGRRRR.DCE.Commands.Mail;

import EDGRRRR.DCE.Mail.Mail;
import EDGRRRR.DCE.Mail.MailList;
import EDGRRRR.DCE.Main.DCEPlugin;
import EDGRRRR.DCE.Math.Math;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class readMail implements CommandExecutor {
    private final DCEPlugin app;
    private final String usage = "/readMail | /readMail <page>";

    public readMail(DCEPlugin app) {
         this.app = app;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(this.app.getConfigManager().strComReadMail))) {
            this.app.getConsoleManager().severe(player, "This command is not enabled.");
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
                this.app.getConsoleManager().usage(player, "Invalid number of arguments.", this.usage);
                return true;
        }
        if (page < 0) {
            this.app.getConsoleManager().usage(player, "Invalid page.", this.usage);

        } else {

            MailList mailList = this.app.getMailManager().getMailList(player);

            if (mailList.getAllMail().isEmpty()) {
                this.app.getConsoleManager().warn(player, "You have no mail.");
            } else {
                HashMap<Integer, Mail[]> mailPages = mailList.getPages(pageSize);

                if ((page + 1) > mailPages.size()) {
                    this.app.getConsoleManager().warn(player, "Invalid page. Choose a number up to " + mailPages.size());
                } else {
                    Mail[] mailPage = mailPages.get(page);
                    this.app.getConsoleManager().info(player, "Mail List (" + (page + 1) + "/" + mailPages.size() + ")");
                    int idx = 1;
                    for (Mail mail : mailPage) {
                        if (mail == null) {
                            break;
                        }
                        this.app.getConsoleManager().info(player, idx + ": " + mail.getMessage());
                        mail.setRead(true);
                        idx += 1;
                    }
                }
            }
        }
        return true;
    }
}
