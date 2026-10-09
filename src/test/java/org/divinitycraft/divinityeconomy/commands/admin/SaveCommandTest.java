package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class SaveCommandTest extends CommandTestBase {

    @Test
    void save_materialsSavesTheMaterialMarket() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "save materials");

        assertAnyMessageContains(op, "saved");
    }
}
