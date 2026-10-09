package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SellAllCommandTest extends CommandTestBase {

    @Test
    void sellAll_withNoArgsSellsEveryMarketableItem() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.getInventory().addItem(new ItemStack(Material.STONE, 10), new ItemStack(Material.DIRT, 10));

        sendCommand(player, "sellall");

        assertTrue(player.getInventory().all(Material.STONE).isEmpty());
        assertTrue(player.getInventory().all(Material.DIRT).isEmpty());
        assertTrue(balanceOf(player) > 0.0);
    }

    @Test
    void sellAll_withAnEmptyInventoryReportsNothingToSell() {
        PlayerMock player = addPlayer("Steve", 0.0);

        sendCommand(player, "sellall");

        assertAnyMessageContains(player, "nothing to sell");
        assertEquals(0.0, balanceOf(player), 0.0001);
    }
}
