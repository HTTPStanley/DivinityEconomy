package org.divinitycraft.divinityeconomy.commands.experience;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienceBuyCommandTest extends CommandTestBase {

    @Test
    void xpBuy_withEnoughFundsGrantsExperienceAndDeductsBalance() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);

        sendCommand(player, "xpBuy 10");

        assertTrue(player.getLevel() > 0 || player.getExp() > 0, "Expected the player to gain experience");
        assertTrue(balanceOf(player) < 1_000_000.0);
    }

    @Test
    void xpBuy_withInsufficientFundsDoesNotGrantExperience() {
        PlayerMock player = addPlayer("Steve", 0.0);

        sendCommand(player, "xpBuy 10");

        assertEquals(0, player.getLevel());
        assertEquals(0.0, balanceOf(player), 0.0001);
    }
}
