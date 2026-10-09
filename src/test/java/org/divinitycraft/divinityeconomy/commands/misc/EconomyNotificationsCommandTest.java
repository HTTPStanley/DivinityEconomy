package org.divinitycraft.divinityeconomy.commands.misc;

import org.divinitycraft.divinityeconomy.commands.CommandTestBase;
import org.divinitycraft.divinityeconomy.economy.players.EconomyPlayer;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EconomyNotificationsCommandTest extends CommandTestBase {

    @Test
    void economyNotifications_withNoArgsTogglesCurrentState() {
        PlayerMock player = addPlayer("Steve");
        EconomyPlayer economyPlayer = plugin.getEconMan().getPlayer(player);
        boolean before = economyPlayer.getNotification();

        sendCommand(player, "economyNotifications");

        assertTrue(economyPlayer.getNotification() != before);
    }

    @Test
    void economyNotifications_withExplicitArgSetsState() {
        PlayerMock player = addPlayer("Steve");

        sendCommand(player, "economyNotifications false");

        assertTrue(!plugin.getEconMan().getPlayer(player).getNotification());
    }
}
