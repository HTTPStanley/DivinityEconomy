package org.divinitycraft.divinityeconomy.commands.misc;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PingCommandTest extends CommandTestBase {

    @Test
    void ping_respondsToPlayer() {
        PlayerMock player = addPlayer("Steve");

        boolean handled = sendCommand(player, "ping");

        assertTrue(handled);
        String message = nextMessageStripped(player);
        assertTrue(message.length() > 0, "Expected a non-empty ping response");
    }
}
