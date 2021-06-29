package me.edgrrrr.de.events;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.mail.MailList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A mail event for telling players if they have pending mail or not
 */
public class MailEvent implements Listener {
    private final DEPlugin app;

    public MailEvent(DEPlugin app) {
        this.app = app;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MailList mailList = this.app.getMailManager().getMailList(player.getUniqueId().toString());
        if (mailList.hasMail()) {
            this.app.getConsole().info(player, "You have %s previously read and %s unread mail", mailList.getReadMail().size(), mailList.getUnreadMail().size());
        } else {
            this.app.getConsole().info(player, "You have no mail. ");
        }
    }
}
