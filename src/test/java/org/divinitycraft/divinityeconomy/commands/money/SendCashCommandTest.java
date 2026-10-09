package org.divinitycraft.divinityeconomy.commands.money;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendCashCommandTest extends CommandTestBase {

    @Test
    void sendCash_transfersBalanceBetweenPlayers() {
        PlayerMock sender = addPlayer("Steve", 100.0);
        PlayerMock receiver = addPlayer("Alex", 0.0);

        sendCommand(sender, "sendcash Alex 25");

        assertEquals(75.0, balanceOf(sender), 0.0001);
        assertEquals(25.0, balanceOf(receiver), 0.0001);
    }

    @Test
    void sendCash_failsWithInsufficientFunds() {
        PlayerMock sender = addPlayer("Steve", 10.0);
        PlayerMock receiver = addPlayer("Alex", 0.0);

        sendCommand(sender, "sendcash Alex 1000");

        assertEquals(10.0, balanceOf(sender), 0.0001);
        assertEquals(0.0, balanceOf(receiver), 0.0001);
    }

    @Test
    void sendCash_toSelfIsRejected() {
        PlayerMock sender = addPlayer("Steve", 100.0);

        sendCommand(sender, "sendcash Steve 10");

        // Balance alone can't distinguish "rejected" from "sent 10 to self and got it straight
        // back" (both leave the balance at 100), so assert the explicit failure message too.
        assertEquals(100.0, balanceOf(sender), 0.0001);
        assertAnyMessageContains(sender, "yourself");
    }

    @Test
    void sendCash_toUnknownPlayerIsRejected() {
        PlayerMock sender = addPlayer("Steve", 100.0);

        boolean handled = sendCommand(sender, "sendcash DoesNotExist 10");

        assertTrue(handled);
        assertEquals(100.0, balanceOf(sender), 0.0001);
    }

    @Test
    void sendCash_withWrongNumberOfArgsShowsUsage() {
        PlayerMock sender = addPlayer("Steve", 100.0);

        sendCommand(sender, "sendcash Alex");

        assertEquals(100.0, balanceOf(sender), 0.0001);
    }
}
