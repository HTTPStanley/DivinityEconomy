package edgrrrr.dce.commands.mail;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.help.Help;
import edgrrrr.dce.mail.Mail;
import edgrrrr.dce.mail.MailList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A command for clearing mail
 */
public class ClearMail implements CommandExecutor {
    private final DCEPlugin app;
    private final Help help;

    public ClearMail(DCEPlugin app) {
        this.app = app;
        this.help = this.app.getHelpManager().get("clearmail");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        // Ensure command is enabled
        if (!(this.app.getConfig().getBoolean(Setting.COMMAND_CLEAR_MAIL_ENABLE_BOOLEAN.path()))) {
            DCEPlugin.CONSOLE.severe(player, "This command is not enabled.");
            return true;
        }

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
                        DCEPlugin.CONSOLE.usage(player, "Invalid arguments.", this.help);
                        return true;
                }
                break;

            default:
                DCEPlugin.CONSOLE.usage(player, "Invalid number of arguments.", this.help);
                return true;
        }

        MailList mailList = this.app.getMailManager().getMailList(player.getUniqueId().toString());
        HashMap<String, Mail> allMail = mailList.getAllMail();
        ArrayList<String> readMail = mailList.getReadMail();
        ArrayList<String> unreadMail = mailList.getUnreadMail();
        ArrayList<String> mailToClear = new ArrayList<>();
        int readMailCleared = 0;
        int unreadMailCleared = 0;

        if (allMail.isEmpty()) {
            DCEPlugin.CONSOLE.warn(player, "You have no mail to clear.");
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
            DCEPlugin.CONSOLE.info(player, String.format("Removed %d mail. (%d unread & %d read)", mailToClear.size(), unreadMailCleared, readMailCleared));

        }
        return true;
    }
}
