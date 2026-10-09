package org.divinitycraft.divinityeconomy.commands.market;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class HandInfoCommandTest extends CommandTestBase {

    @Test
    void handInformation_reportsDetailsForHeldItem() {
        PlayerMock player = addPlayer("Steve");
        player.getInventory().setItemInMainHand(new ItemStack(Material.STONE, 1));

        sendCommand(player, "handInformation");

        assertAllMessagesContain(player, "Information for", "Is Banned:");
    }

    @Test
    void handInformation_withEmptyHandFallsBackToAir() {
        PlayerMock player = addPlayer("Steve");

        boolean handled = sendCommand(player, "handInformation");

        org.junit.jupiter.api.Assertions.assertTrue(handled);
        assertAnyMessageContains(player, "Information for");
    }
}
