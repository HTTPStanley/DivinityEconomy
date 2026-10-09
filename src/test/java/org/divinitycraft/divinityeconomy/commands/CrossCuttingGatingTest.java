package org.divinitycraft.divinityeconomy.commands;

import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

/**
 * Covers the gating logic shared by every command through the DivinityCommand/DivinityCommandTC
 * base classes (permission checks, console support), using a couple of representative commands
 * rather than repeating the same checks 27 times.
 */
class CrossCuttingGatingTest extends CommandTestBase {

    @Test
    void commandWithoutConsoleSupportRejectsConsoleSender() {
        // Buy has hasConsoleSupport=false
        boolean handled = sendConsoleCommand("buy STONE 1");

        assertAnyMessageContains(server.getConsoleSender(), "does not support console");
        org.junit.jupiter.api.Assertions.assertTrue(handled);
    }

    @Test
    void commandWithConsoleSupportAllowsConsoleSender() {
        // Balance has hasConsoleSupport=true and a console-usable code path
        addPlayer("Steve", 100.0);

        boolean handled = sendConsoleCommand("balance Steve");

        assertAnyMessageContains(server.getConsoleSender(), "Steve");
        org.junit.jupiter.api.Assertions.assertTrue(handled);
    }

    @Test
    void nonOpDeniedAccessToOpOnlyAdminCommand() {
        PlayerMock nonOp = addPlayer("Steve", 0.0);

        sendCommand(nonOp, "setbal 100");

        // de.admin.setbal defaults to "op" - a fresh non-op player has no such permission
        assertAnyMessageContains(nonOp, "permission");
    }

    @Test
    void opGrantedAccessToOpOnlyAdminCommand() {
        PlayerMock op = addPlayer("Admin", 0.0);
        op.setOp(true);

        boolean handled = sendCommand(op, "setbal 100");

        org.junit.jupiter.api.Assertions.assertTrue(handled);
        assertAnyMessageContains(op, "100");
    }
}
