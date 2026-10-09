package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandSellCommandTest extends CommandTestBase {

    @Test
    void handSell_sellsTheHeldItem() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.getInventory().setItemInMainHand(new ItemStack(Material.STONE, 10));

        sendCommand(player, "handSell 5");

        assertEquals(5, player.getInventory().all(Material.STONE).values().stream().mapToInt(ItemStack::getAmount).sum());
        assertTrue(balanceOf(player) > 0.0);
    }

    @Test
    void handSell_withEmptyHandIsRejected() {
        PlayerMock player = addPlayer("Steve", 0.0);

        sendCommand(player, "handSell 5");

        assertAnyMessageContains(player, "holding an item");
        assertEquals(0.0, balanceOf(player), 0.0001);
    }
}
