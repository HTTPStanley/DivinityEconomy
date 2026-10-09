package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SellCommandTest extends CommandTestBase {

    @Test
    void sell_withEnoughStockRemovesItemsAndAddsBalance() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.getInventory().addItem(new ItemStack(Material.STONE, 10));

        sendCommand(player, "sell STONE 5");

        assertEquals(5, player.getInventory().all(Material.STONE).values().stream().mapToInt(ItemStack::getAmount).sum());
        assertTrue(balanceOf(player) > 0.0);
    }

    @Test
    void sell_withoutEnoughInInventoryIsRejected() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.getInventory().addItem(new ItemStack(Material.STONE, 2));

        sendCommand(player, "sell STONE 5");

        assertEquals(2, player.getInventory().all(Material.STONE).values().stream().mapToInt(ItemStack::getAmount).sum());
        assertEquals(0.0, balanceOf(player), 0.0001);
    }

    @Test
    void sell_withMaxKeywordSellsEntireStack() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.getInventory().addItem(new ItemStack(Material.STONE, 20));

        sendCommand(player, "sell STONE max");

        assertTrue(player.getInventory().all(Material.STONE).isEmpty());
        assertTrue(balanceOf(player) > 0.0);
    }
}
