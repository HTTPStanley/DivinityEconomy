package me.edgrrrr.de.mail.events;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.mail.MailList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A mail event for telling players if they have pending mail or not
 */
public class MailEvent implements Listener {
    private final DEPlugin main;

    public MailEvent(DEPlugin app) {
        this.main = app;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MailList mailList = this.main.getMailMan().getMailList(player.getUniqueId().toString());
        if (mailList.hasMail()) {
            this.main.getConsole().info(player, LangEntry.MAIL_MailNotification.get(this.main), mailList.getAllMail().size());
        } else {
            this.main.getConsole().info(player, LangEntry.MAIL_NoMailNotification.get(this.main));
        }
    }
}
