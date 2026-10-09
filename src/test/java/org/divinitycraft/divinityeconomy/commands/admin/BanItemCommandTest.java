package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanItemCommandTest extends CommandTestBase {

    @Test
    void banItem_trueDisallowsTheMaterial() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "banItem STONE true");

        assertFalse(plugin.getMatMan().getItem("STONE").getAllowed());
    }

    @Test
    void banItem_falseAllowsTheMaterialAgain() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        sendCommand(op, "banItem STONE true");

        sendCommand(op, "banItem STONE false");

        assertTrue(plugin.getMatMan().getItem("STONE").getAllowed());
    }
}
