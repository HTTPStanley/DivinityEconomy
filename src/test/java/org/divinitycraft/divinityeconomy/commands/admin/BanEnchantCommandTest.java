package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BanEnchantCommandTest extends CommandTestBase {

    @Test
    void banEnchant_trueDisallowsTheEnchant() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);

        sendCommand(op, "banEnchant SHARPNESS true");

        assertFalse(plugin.getEnchMan().getEnchant("SHARPNESS").getAllowed());
    }

    @Test
    void banEnchant_falseAllowsTheEnchantAgain() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        sendCommand(op, "banEnchant SHARPNESS true");

        sendCommand(op, "banEnchant SHARPNESS false");

        assertTrue(plugin.getEnchMan().getEnchant("SHARPNESS").getAllowed());
    }
}
