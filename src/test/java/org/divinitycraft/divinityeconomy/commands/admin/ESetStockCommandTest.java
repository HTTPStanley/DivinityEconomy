package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ESetStockCommandTest extends CommandTestBase {

    @Test
    void eSetStock_updatesTheQuantityOfAnEnchant() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "eSetStock SHARPNESS 500");

        assertEquals(500, plugin.getEnchMan().getEnchant("SHARPNESS").getQuantity());
    }
}
