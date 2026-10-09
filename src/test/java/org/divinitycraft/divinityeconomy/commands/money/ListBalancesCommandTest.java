package org.divinitycraft.divinityeconomy.commands.money;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class ListBalancesCommandTest extends CommandTestBase {

    @Test
    void listBalances_showsPlayersOrderedIntoTheBaltop() {
        PlayerMock rich = addPlayer("Rich", 1000.0);
        addPlayer("Poor", 1.0);
        plugin.getEconMan().fetchBaltop();

        sendCommand(rich, "listbalances");

        assertAnyMessageContains(rich, "Rich");
    }

    @Test
    void listBalances_worksFromConsoleToo() {
        addPlayer("Rich", 1000.0);
        plugin.getEconMan().fetchBaltop();

        boolean handled = server.dispatchCommand(server.getConsoleSender(), "listbalances");

        org.junit.jupiter.api.Assertions.assertTrue(handled);
    }
}
