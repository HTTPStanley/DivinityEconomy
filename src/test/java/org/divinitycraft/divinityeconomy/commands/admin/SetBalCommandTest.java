package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetBalCommandTest extends CommandTestBase {

    @Test
    void setBal_setsAnotherPlayersBalanceExactly() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        PlayerMock target = addPlayer("Steve", 999.0);

        sendCommand(op, "setbal Steve 42");

        assertEquals(42.0, balanceOf(target), 0.0001);
    }

    @Test
    void setBal_withoutOpPermissionIsDenied() {
        PlayerMock nonOp = addPlayer("Steve");
        PlayerMock target = addPlayer("Alex", 100.0);

        sendCommand(nonOp, "setbal Alex 42");

        assertEquals(100.0, balanceOf(target), 0.0001);
    }
}
