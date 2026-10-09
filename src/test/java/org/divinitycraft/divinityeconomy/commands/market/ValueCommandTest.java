package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class ValueCommandTest extends CommandTestBase {

    @Test
    void value_reportsBuyAndSellPricesForAValidMaterial() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "value STONE 10");

        assertAllMessagesContain(player, "Buy:", "Sell:");
    }

    @Test
    void value_forInvalidMaterialIsRejected() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "value NOT_A_REAL_MATERIAL 1");

        assertAnyMessageContains(player, "Invalid item name");
    }
}
