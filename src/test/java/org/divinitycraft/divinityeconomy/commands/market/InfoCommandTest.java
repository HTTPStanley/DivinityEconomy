package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class InfoCommandTest extends CommandTestBase {

    @Test
    void information_reportsDetailsForAValidMaterial() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "information STONE");

        assertAllMessagesContain(player, "Information for", "Is Banned:");
    }

    @Test
    void information_forUnknownMaterialIsRejected() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "information NOT_A_REAL_MATERIAL");

        assertAnyMessageContains(player, "Unknown item");
    }
}
