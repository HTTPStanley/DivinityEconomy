package edgrrrr.dce.events;

import edgrrrr.dce.mail.MailList;
import edgrrrr.dce.DCEPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A mail event for telling players if they have pending mail or not
 */
public class MailEvent implements Listener {
    private final DCEPlugin app;

    public MailEvent(DCEPlugin app) {
        this.app = app;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MailList mailList = this.app.getMailManager().getMailList(player);
        if (mailList.hasMail()) {
            DCEPlugin.CONSOLE.info(player, "You have " + mailList.getReadMail().size() + " read and " + mailList.getUnreadMail().size() + " unread mail");
        } else {
            DCEPlugin.CONSOLE.info(player, "You have no mail.");
        }
    }
}
