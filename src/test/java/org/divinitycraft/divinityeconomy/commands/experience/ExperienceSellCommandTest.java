package org.divinitycraft.divinityeconomy.commands.experience;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienceSellCommandTest extends CommandTestBase {

    @Test
    void xpSell_withEnoughExperienceAddsBalance() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.setLevel(10);

        sendCommand(player, "xpSell 10");

        assertTrue(balanceOf(player) > 0.0);
    }

    @Test
    void xpSell_withNoExperienceDoesNotAddBalance() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.setLevel(0);
        player.setExp(0f);

        sendCommand(player, "xpSell 10");

        assertEquals(0.0, balanceOf(player), 0.0001);
    }
}
