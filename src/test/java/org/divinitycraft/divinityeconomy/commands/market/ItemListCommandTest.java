package org.divinitycraft.divinityeconomy.commands.market;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

class ItemListCommandTest extends CommandTestBase {

    @Test
    void listItems_withNoArgsShowsAlphabeticalFirstPage() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "listitems");

        assertAnyMessageContains(player, "Item List");
    }

    @Test
    void listItems_searchingByNameFiltersResults() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "listitems STONE");

        assertAnyMessageContains(player, "STONE");
    }
}
