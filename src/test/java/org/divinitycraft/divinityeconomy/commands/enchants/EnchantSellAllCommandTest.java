package org.divinitycraft.divinityeconomy.commands.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnchantSellAllCommandTest extends CommandTestBase {

    @Test
    void eSellAll_sellsAllEnchantsAcrossInventory() {
        PlayerMock player = addPlayer("Steve", 0.0);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
        player.getInventory().addItem(sword);

        sendCommand(player, "eSellAll");

        assertTrue(balanceOf(player) > 0.0);
    }

    @Test
    void eSellAll_withNoEnchantedItemsReportsNothingToSell() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.getInventory().addItem(new ItemStack(Material.STONE, 5));

        sendCommand(player, "eSellAll");

        assertAnyMessageContains(player, "nothing left to sell");
        assertEquals(0.0, balanceOf(player), 0.0001);
    }
}
