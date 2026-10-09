package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.Material;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuyCommandTest extends CommandTestBase {

    @Test
    void buy_withEnoughFundsAddsItemsAndDeductsBalance() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);

        sendCommand(player, "buy STONE 5");

        assertEquals(5, player.getInventory().all(Material.STONE).values().stream().mapToInt(i -> i.getAmount()).sum());
        assertTrue(balanceOf(player) < 1_000_000.0);
    }

    @Test
    void buy_withInsufficientFundsDoesNotGiveItems() {
        PlayerMock player = addPlayer("Steve", 0.0);

        sendCommand(player, "buy STONE 5");

        assertTrue(player.getInventory().all(Material.STONE).isEmpty());
        assertEquals(0.0, balanceOf(player), 0.0001);
    }

    @Test
    void buy_withInvalidMaterialNameIsRejected() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);

        sendCommand(player, "buy NOT_A_REAL_MATERIAL 5");

        assertAnyMessageContains(player, "Invalid item name");
        assertEquals(1_000_000.0, balanceOf(player), 0.0001);
    }
}
