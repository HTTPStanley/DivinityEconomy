package org.divinitycraft.divinityeconomy.commands.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnchantHandBuyCommandTest extends CommandTestBase {

    @Test
    void eBuy_addsTheEnchantToTheHeldItem() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        sendCommand(player, "eBuy SHARPNESS 1");

        ItemStack held = player.getInventory().getItemInMainHand();
        assertEquals(1, held.getEnchantmentLevel(Enchantment.SHARPNESS));
        assertTrue(balanceOf(player) < 1_000_000.0);
    }

    @Test
    void eBuy_withEmptyHandIsRejected() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);

        sendCommand(player, "eBuy SHARPNESS 1");

        assertAnyMessageContains(player, "holding an item");
        assertEquals(1_000_000.0, balanceOf(player), 0.0001);
    }

    @Test
    void eBuy_withInvalidEnchantNameIsRejected() {
        PlayerMock player = addPlayer("Steve", 1_000_000.0);
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        sendCommand(player, "eBuy NOT_A_REAL_ENCHANT 1");

        assertEquals(1_000_000.0, balanceOf(player), 0.0001);
    }
}
