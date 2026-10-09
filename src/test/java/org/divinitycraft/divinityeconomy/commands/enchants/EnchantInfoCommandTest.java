package org.divinitycraft.divinityeconomy.commands.enchants;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class EnchantInfoCommandTest extends CommandTestBase {

    @Test
    void eInfo_reportsDetailsForAValidEnchant() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "eInfo SHARPNESS");

        assertAllMessagesContain(player, "Information for", "Is Banned:");
    }

    @Test
    void eInfo_forUnknownEnchantIsRejected() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "eInfo NOT_A_REAL_ENCHANT");

        assertAnyMessageContains(player, "Unknown enchant");
    }
}
