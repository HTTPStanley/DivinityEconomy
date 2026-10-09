package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class HandValueCommandTest extends CommandTestBase {

    @Test
    void handValue_reportsBuyAndSellPricesForHeldItem() {
        PlayerMock player = addPlayer("Steve");
        player.getInventory().setItemInMainHand(new ItemStack(Material.STONE, 5));

        sendCommand(player, "handValue");

        assertAllMessagesContain(player, "Buy:", "Sell:");
    }

    @Test
    void handValue_withEmptyHandIsRejected() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "handValue");

        assertAnyMessageContains(player, "holding an item");
    }
}
