package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandBuyCommandTest extends CommandTestBase {

    @Test
    void handBuy_buysMoreOfTheHeldItem() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);
        player.getInventory().setItemInMainHand(new ItemStack(Material.STONE, 1));

        sendCommand(player, "handBuy 5");

        assertEquals(6, player.getInventory().all(Material.STONE).values().stream().mapToInt(ItemStack::getAmount).sum());
        assertTrue(balanceOf(player) < 1_000_000.0);
    }

    @Test
    void handBuy_withEmptyHandIsRejected() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);

        sendCommand(player, "handBuy 5");

        assertTrue(player.getInventory().all(Material.STONE).isEmpty());
        assertEquals(1_000_000.0, balanceOf(player), 0.0001);
    }
}
