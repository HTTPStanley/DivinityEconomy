package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ESetValueCommandTest extends CommandTestBase {

    @Test
    void eSetValue_updatesTheConfiguredPriceOfAnEnchant() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "eSetValue SHARPNESS 7.0");

        assertEquals(7.0, plugin.getEnchMan().getEnchant("SHARPNESS").getPrice(), 0.0001);
    }
}
