package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetValueCommandTest extends CommandTestBase {

    @Test
    void setValue_updatesTheConfiguredPriceOfAMaterial() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "setValue STONE 5.0");

        assertEquals(5.0, plugin.getMatMan().getItem("STONE").getPrice(), 0.0001);
    }

    @Test
    void setValue_withNegativeValueIsRejected() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        double before = plugin.getMatMan().getItem("STONE").getPrice();

        sendCommand(op, "setValue STONE -5.0");

        assertEquals(before, plugin.getMatMan().getItem("STONE").getPrice(), 0.0001);
    }
}
