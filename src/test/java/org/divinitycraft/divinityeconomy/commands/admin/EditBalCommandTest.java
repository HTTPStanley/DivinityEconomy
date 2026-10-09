package org.divinitycraft.divinityeconomy.commands.admin;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditBalCommandTest extends CommandTestBase {

    @Test
    void editBal_addsAPositiveAmountToAnotherPlayer() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        PlayerMock target = addPlayer("Steve", 100.0);

        sendCommand(op, "editbal Steve 50");

        assertEquals(150.0, balanceOf(target), 0.0001);
    }

    @Test
    void editBal_subtractsANegativeAmount() {
        PlayerMock op = addPlayer("Admin");
        op.setOp(true);
        PlayerMock target = addPlayer("Steve", 100.0);

        sendCommand(op, "editbal Steve -30");

        assertEquals(70.0, balanceOf(target), 0.0001);
    }

    @Test
    void editBal_withoutOpPermissionIsDenied() {
        PlayerMock nonOp = addPlayer("Steve");
        PlayerMock target = addPlayer("Alex", 100.0);

        sendCommand(nonOp, "editbal Alex 50");

        assertEquals(100.0, balanceOf(target), 0.0001);
    }
}
