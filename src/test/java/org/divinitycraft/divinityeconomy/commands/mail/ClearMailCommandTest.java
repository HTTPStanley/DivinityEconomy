package org.divinitycraft.divinityeconomy.commands.mail;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.divinitycraft.divinityeconomy.mail.MailList;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClearMailCommandTest extends CommandTestBase {

    @Test
    void clearMail_all_removesEveryMailEntry() {
        PlayerMock player = addPlayer("Steve");
        MailList mailList = plugin.getMailMan().getMailList(player.getUniqueId().toString());
        mailList.createMail("Mail 1");
        mailList.createMail("Mail 2");

        sendCommand(player, "clearMail all");

        assertTrue(mailList.getAllMail().isEmpty());
    }

    @Test
    void clearMail_withNoMailReportsNothingToClear() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "clearMail all");

        assertAnyMessageContains(player, "no mail to clear");
    }
}
