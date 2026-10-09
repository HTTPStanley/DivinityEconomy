package org.divinitycraft.divinityeconomy.commands;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Base class for end-to-end command tests. Boots the real plugin (real onEnable, real
 * command registration) against a MockBukkit-simulated server, backed by the plugin's own
 * Vault Economy implementation (a bare "Vault" plugin is registered so EconomyManager's
 * presence check passes; DivinityEconomy itself supplies the Economy provider).
 */
public abstract class CommandTestBase {

    protected ServerMock server;
    protected DEPlugin plugin;

    @BeforeEach
    void setUpServer() {
        server = MockBukkit.mock();
        MockBukkit.createMockPlugin("Vault");
        plugin = MockBukkit.load(DEPlugin.class);
    }

    @AfterEach
    void tearDownServer() {
        MockBukkit.unmock();
    }

    /**
     * Creates a player with a starting balance, bypassing the command layer.
     */
    protected PlayerMock addPlayer(String name, double startingBalance) {
        PlayerMock player = server.addPlayer(name);
        plugin.getEconMan().getVaultEconomy().depositPlayer(player, startingBalance);
        refreshOfflinePlayerCache();
        return player;
    }

    /**
     * PlayerManager snapshots the server's offline/online players once at plugin init and only
     * refreshes on a 60s repeating task. Since players are added to the mock server after the
     * plugin has already booted, force a refresh so newly added players are visible to lookups
     * like /balance <name>, /sendcash, and baltop.
     */
    protected void refreshOfflinePlayerCache() {
        try {
            java.lang.reflect.Method method = plugin.getPlayMan().getClass().getDeclaredMethod("fetchOfflinePlayers");
            method.setAccessible(true);
            method.invoke(plugin.getPlayMan());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected PlayerMock addPlayer(String name) {
        return addPlayer(name, 0);
    }

    protected double balanceOf(PlayerMock player) {
        return plugin.getEconMan().getVaultEconomy().getBalance(player);
    }

    /**
     * Dispatches a command as if typed by the player (no leading slash).
     */
    protected boolean sendCommand(PlayerMock player, String command) {
        return player.performCommand(command);
    }

    /**
     * Dispatches a command as if typed into the server console (no leading slash).
     */
    protected boolean sendConsoleCommand(String command) {
        return server.dispatchCommand(server.getConsoleSender(), command);
    }

    /**
     * Drains and returns all messages queued for the target since the last read, stripped
     * of colour codes for readable assertions. Works for both PlayerMock and the console
     * sender (both implement MockBukkit's MessageTarget).
     */
    protected String nextMessageStripped(org.mockbukkit.mockbukkit.command.MessageTarget target) {
        String message = target.nextMessage();
        if (message == null) {
            fail("Expected a message to be sent but none was queued");
        }
        return stripColour(message);
    }

    protected Queue<String> drainMessagesStripped(org.mockbukkit.mockbukkit.command.MessageTarget target) {
        Queue<String> messages = new ArrayDeque<>();
        String message;
        while ((message = target.nextMessage()) != null) {
            messages.add(stripColour(message));
        }
        return messages;
    }

    protected void assertNoMoreMessages(org.mockbukkit.mockbukkit.command.MessageTarget target) {
        assertTrue(target.nextMessage() == null, "Expected no further messages to be queued");
    }

    protected void assertAnyMessageContains(org.mockbukkit.mockbukkit.command.MessageTarget target, String expectedSubstring) {
        assertAllMessagesContain(target, expectedSubstring);
    }

    /**
     * Drains the target's message queue once and asserts every given substring is present in at
     * least one message. Use this (rather than multiple calls to assertAnyMessageContains) when
     * checking for more than one substring after a single command, since draining is destructive.
     */
    protected void assertAllMessagesContain(org.mockbukkit.mockbukkit.command.MessageTarget target, String... expectedSubstrings) {
        Queue<String> messages = drainMessagesStripped(target);
        for (String expectedSubstring : expectedSubstrings) {
            boolean found = messages.stream().anyMatch(m -> m.contains(expectedSubstring));
            if (!found) {
                fail("Expected one of the messages to contain '" + expectedSubstring + "' but got: " + messages);
            }
        }
    }

    private static String stripColour(String message) {
        return message.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
    }
}
