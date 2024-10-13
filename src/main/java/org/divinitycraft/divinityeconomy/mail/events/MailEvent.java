package org.divinitycraft.divinityeconomy.mail.events;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.mail.MailList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * A mail event for telling players if they have pending mail or not
 */
public class MailEvent implements Listener {
    private final DEPlugin main;
    private final boolean silent;

    public MailEvent(DEPlugin app, boolean silent) {
        this.main = app;
        this.silent = silent;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MailList mailList = this.main.getMailMan().getMailList(player.getUniqueId().toString());
        if (mailList.hasMail()) {
            this.main.getConsole().info(player, LangEntry.MAIL_MailNotification.get(this.main), mailList.getAllMail().size());
        } else if (!this.silent) {
            this.main.getConsole().info(player, LangEntry.MAIL_NoMailNotification.get(this.main));
        }
    }
}
