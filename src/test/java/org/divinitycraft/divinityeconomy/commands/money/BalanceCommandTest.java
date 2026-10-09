package org.divinitycraft.divinityeconomy.commands.money;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BalanceCommandTest extends CommandTestBase {

    @Test
    void balance_withNoArgsReturnsCallersOwnBalance() {
        PlayerMock player = addPlayer("Steve", 250.0);

        boolean handled = sendCommand(player, "balance");

        assertTrue(handled);
        assertAnyMessageContains(player, "Steve");
    }

    @Test
    void balance_withPlayerArgAndOpPermissionReturnsThatPlayersBalance() {
        PlayerMock op = addPlayer("Admin", 0.0);
        op.setOp(true);
        addPlayer("Alex", 500.0);

        sendCommand(op, "balance Alex");

        assertAnyMessageContains(op, "Alex");
    }

    @Test
    void balance_withPlayerArgAndExplicitlyRevokedPermissionIsDenied() {
        // de.money.balanceOther has no explicit "default" in plugin.yml, but is listed as a
        // "true" child of the default-true "de.money" node, so it is granted to everyone
        // unless an admin explicitly revokes it (e.g. via a permissions plugin) - simulate that.
        PlayerMock player = addPlayer("Steve", 0.0);
        addPlayer("Alex", 500.0);
        player.addAttachment(server.getPluginManager().getPlugin("DivinityEconomy"), "de.money.balanceOther", false);

        sendCommand(player, "balance Alex");

        assertAnyMessageContains(player, "permission");
    }

    @Test
    void balance_forUnknownPlayerReportsInvalidName() {
        PlayerMock op = addPlayer("Admin", 0.0);
        op.setOp(true);

        sendCommand(op, "balance DoesNotExist");

        assertAnyMessageContains(op, "Invalid");
    }
}
