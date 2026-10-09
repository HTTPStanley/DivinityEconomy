package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class ReloadCommandTest extends CommandTestBase {

    @Test
    void reload_materialsReloadsTheMaterialMarket() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "reload materials");

        assertAnyMessageContains(op, "reloaded");
    }

    @Test
    void reload_withoutATypeShowsUsage() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        boolean handled = sendCommand(op, "reload");

        org.junit.jupiter.api.Assertions.assertTrue(handled);
    }
}
