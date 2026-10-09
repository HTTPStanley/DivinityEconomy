package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetStockCommandTest extends CommandTestBase {

    @Test
    void setStock_updatesTheQuantityOfAMaterial() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "setStock STONE 1000");

        assertEquals(1000, plugin.getMatMan().getItem("STONE").getQuantity());
    }

    @Test
    void setStock_withNegativeStockIsRejected() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        int before = plugin.getMatMan().getItem("STONE").getQuantity();

        sendCommand(op, "setStock STONE -100");

        assertEquals(before, plugin.getMatMan().getItem("STONE").getQuantity());
    }
}
