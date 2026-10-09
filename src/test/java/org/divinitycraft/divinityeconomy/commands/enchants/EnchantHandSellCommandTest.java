package org.divinitycraft.divinityeconomy.commands.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnchantHandSellCommandTest extends CommandTestBase {

    @Test
    void eSell_removesTheEnchantAndPaysThePlayer() {
        PlayerMock player = addPlayer("Steve", 0.0);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
        player.getInventory().setItemInMainHand(sword);

        sendCommand(player, "eSell SHARPNESS 1");

        ItemStack held = player.getInventory().getItemInMainHand();
        assertEquals(0, held.getEnchantmentLevel(Enchantment.SHARPNESS));
        assertTrue(balanceOf(player) > 0.0);
    }

    @Test
    void eSell_withUnenchantedItemIsRejected() {
        PlayerMock player = addPlayer("Steve", 0.0);
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        sendCommand(player, "eSell SHARPNESS 1");

        assertEquals(0.0, balanceOf(player), 0.0001);
    }
}
