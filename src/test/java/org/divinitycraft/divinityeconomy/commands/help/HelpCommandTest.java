package org.divinitycraft.divinityeconomy.commands.help;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class HelpCommandTest extends CommandTestBase {

    @Test
    void ehelp_withNoArgsShowsFirstPage() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "ehelp");

        assertAnyMessageContains(player, "Help page");
    }

    @Test
    void ehelp_withCommandNameShowsThatCommandsHelp() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "ehelp balance");

        assertAnyMessageContains(player, "balance");
    }
}
