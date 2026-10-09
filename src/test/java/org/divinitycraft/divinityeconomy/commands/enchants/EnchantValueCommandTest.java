package org.divinitycraft.divinityeconomy.commands.enchants;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class EnchantValueCommandTest extends CommandTestBase {

    @Test
    void eValue_reportsBuyAndSellPricesForAValidEnchant() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "eValue SHARPNESS 1");

        assertAllMessagesContain(player, "Buy:", "Sell:");
    }

    @Test
    void eValue_forInvalidEnchantIsRejected() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "eValue NOT_A_REAL_ENCHANT 1");

        assertAnyMessageContains(player, "enchant");
    }

    @Test
    void eValue_reportsPricesForEnchantsThatDoNotApplyToSwords() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "eValue LUNGE 1");

        assertAllMessagesContain(player, "Buy:", "Sell:");
    }
}