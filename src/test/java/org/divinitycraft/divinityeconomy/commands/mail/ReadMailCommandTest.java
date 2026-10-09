package org.divinitycraft.divinityeconomy.commands.mail;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.divinitycraft.divinityeconomy.mail.MailList;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class ReadMailCommandTest extends CommandTestBase {

    @Test
    void readMail_showsSeededMailAndMarksItRead() {
        PlayerMock player = addPlayer("Steve");
        MailList mailList = plugin.getMailMan().getMailList(player.getUniqueId().toString());
        mailList.createMail("Welcome to the server!");

        sendCommand(player, "readMail");

        assertAnyMessageContains(player, "Welcome to the server!");
        assertAllMailRead(mailList);
    }

    @Test
    void readMail_withNoMailReportsNone() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "readMail");

        assertAnyMessageContains(player, "no mail");
    }

    private void assertAllMailRead(MailList mailList) {
        org.junit.jupiter.api.Assertions.assertTrue(mailList.getUnreadMail().isEmpty());
    }
}
