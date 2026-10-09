package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClearBalCommandTest extends CommandTestBase {

    @Test
    void clearBal_zeroesAnotherPlayersBalance() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        PlayerMock target = addPlayer("Steve", 500.0);

        sendCommand(op, "clearbal Steve");

        assertEquals(0.0, balanceOf(target), 0.0001);
    }

    @Test
    void clearBal_withoutOpPermissionIsDenied() {
        PlayerMock nonOp = addPlayer("Steve");
        PlayerMock target = addPlayer("Alex", 500.0);

        sendCommand(nonOp, "clearbal Alex");

        assertEquals(500.0, balanceOf(target), 0.0001);
    }
}
