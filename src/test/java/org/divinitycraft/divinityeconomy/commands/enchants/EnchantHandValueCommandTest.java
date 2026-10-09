package org.divinitycraft.divinityeconomy.commands.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class EnchantHandValueCommandTest extends CommandTestBase {

    @Test
    void eHandValue_reportsValueOfEnchantsOnHeldItem() {
        PlayerMock player = addPlayer("Steve");
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 1);
        player.getInventory().setItemInMainHand(sword);

        sendCommand(player, "eHandValue");

        assertAnyMessageContains(player, "Buy:");
    }

    @Test
    void eHandValue_withUnenchantedItemIsRejected() {
        PlayerMock player = addPlayer("Steve");
        player.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));

        sendCommand(player, "eHandValue");

        assertAnyMessageContains(player, "holding an item");
    }
}
