package edgrrrr.dce.console;

import edgrrrr.dce.DCEPlugin;
import edgrrrr.dce.config.Setting;
import edgrrrr.dce.mail.MailList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class Console {
    private DCEPlugin app;
    private ConsoleCommandSender consoleSender;

    // Settings
    private final boolean debugMode;
    private final String chatPrefix;
    private final String consolePrefix;


    private static final String[] variables = {};
    private static final String[] variableValues = {};

    public Console(DCEPlugin app, String version) {
        this.app = app;
        this.consoleSender = Bukkit.getConsoleSender();

        // Get settings
        this.debugMode = (DCEPlugin.CONFIG.getBoolean(Setting.CHAT_DEBUG_OUTPUT_BOOLEAN));
        String prefix = DCEPlugin.CONFIG.getString(Setting.CHAT_PREFIX_STRING);
        String conPrefix = DCEPlugin.CONFIG.getString(Setting.CHAT_CONSOLE_PREFIX).replace("<VERSION>", version);
        this.chatPrefix = insertColours(prefix);
        conPrefix = insertColours(conPrefix);
        this.consolePrefix = insertVariables(conPrefix);
    }

    private static String insertVariables(String string) {
        for (int idx=0; idx < variables.length; idx++) {
            string = string.replace(variables[idx], variableValues[idx]);
        }
        return string;
    }

    private static String insertColours(String string) {
        for (ChatColor colour : ChatColor.values()) {
            string = string.replaceAll(String.format("(&%s)|(%s)", colour.getChar(), colour.name()), colour.toString());
        }
        return string;
    }

    // CONSOLE COMMANDS

    /**
     * Sends a message to the console
     *
     * @param message - The message to send
     */
    private void send(LogLevel level, String message) {
        this.consoleSender.sendMessage(consolePrefix + level.getColour() + message);
    }

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void info(String message) {
        this.send(LogLevel.INFO, message);
    }

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void debug(String message) {
        if (debugMode) this.send(LogLevel.DEBUG, message);
    }

    /**
     * Sends a (default yellow) message to the console
     *
     * @param message - The message to send
     */
    public void warn(String message) {
        this.send(LogLevel.WARNING, message);
    }

    /**
     * Sends a (default red) message to the console
     *
     * @param message - The message to send
     */
    public void severe(String message) {
        this.send(LogLevel.SEVERE, message);
    }

    // PLAYER

    /**
     * Sends a message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    private void send(Player player, LogLevel level, String message) {
        if (player != null) {
            player.sendMessage(chatPrefix + level.getColour() + message);
        } else {
            this.send(level, message);
        }
    }

    /**
     * Sends a usage command to a player
     *
     * @param player       - The player to send to
     * @param errorMessage - The message to send
     * @param commandUsage - The usage of the command
     */
    public void usage(Player player, String errorMessage, String commandUsage) {
        this.warn(player, "Incorrect command usage: " + errorMessage);
        this.warn(player, "Command Usage: " + commandUsage);
    }

    /**
     * Sends an info message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void info(Player player, String message) {
        this.send(player, LogLevel.INFO, message);
    }

    /**
     * Sends a warning message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void warn(Player player, String message) {
        this.send(player, LogLevel.WARNING, message);
    }

    /**
     * Sends a severe message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void severe(Player player, String message) {
        this.send(player, LogLevel.SEVERE, message);
    }

    /**
     * Handles console logger, player messages and player mail for the completion of a transfer
     * @param player1 - The sender
     * @param player2 - The receiver
     * @param amount - The amount sent
     */
    public void logTransfer(OfflinePlayer player1, OfflinePlayer player2, double amount) {
        amount = this.app.getEconomyManager().round(amount);
        // Send console log of transaction
        this.info(String.format("%s sent £%,.2f to %s", player1.getName(), amount, player2.getName()));

        // Handles online & offline messages for sender
        Player onlinePlayer1 = player1.getPlayer();
        MailList player1MailList = this.app.getMailManager().getMailList(player1);
        String player1Message = String.format("Sent £%,.2f to %s", amount, player2.getName());
        if (onlinePlayer1 != null) {
            this.info(onlinePlayer1, player1Message);
        } else {
            player1MailList.createMail(String.format("%s <daysAgo> days ago", player1Message));
        }

        // Handle online & offline messages for receiver
        Player onlinePlayer2 = player2.getPlayer();
        MailList player2MailList = this.app.getMailManager().getMailList(player2);
        String player2Message = String.format("Received £%,.2f from %s", amount, player1.getName());
        if (onlinePlayer2 != null) {
            this.info(onlinePlayer2, player2Message);
        } else {
            player2MailList.createMail(String.format("%s <daysAgo> days ago", player2Message));
        }
    }

    /**
     * Handles console logging, player messages and player mail for the failure of a transfer
     * @param player1 - The sender
     * @param player2 - The receiver
     * @param amount - The amount sent
     * @param error - The error
     */
    public void logFailedTransfer(OfflinePlayer player1, OfflinePlayer player2, double amount, String error) {
        amount = this.app.getEconomyManager().round(amount);
        // Send console log of transaction
        this.warn(String.format("%s couldn't send £%,.2f to %s because %s", player1.getName(), this.app.getEconomyManager().round(amount), player2.getName(), error));

        // Handles online and offline messages for sender
        Player onlinePlayer1 = player1.getPlayer();
        MailList player1MailList = this.app.getMailManager().getMailList(player1);
        String player1Message = String.format("Couldn't send £%,.2f to %s because %s", amount, player2.getName(), error);
        if (onlinePlayer1 != null) {
            this.warn(onlinePlayer1, player1Message);
        } else {
            player1MailList.createMail(player1Message);
        }
    }

    /**
     * Handles console logging, player messaging and player mail for the successful changing of a players balance.
     * Player1 and Player 2 can be the same!
     * @param player1 - The command sender
     * @param player2 - The command receiver
     * @param balance1 - The balance before the change
     * @param balance2 - The balance after the change
     * @param reason - The reason for the change.
     */
    public void logBalance(OfflinePlayer player1, OfflinePlayer player2, double balance1, double balance2, String reason) {
        // Round balances
        balance1 = this.app.getEconomyManager().round(balance1);
        balance2 = this.app.getEconomyManager().round(balance2);

        // Get the change
        double delta = this.app.getEconomyManager().round(balance2 - balance1);

        // Send console log of balance change
        this.info(String.format("%s's balance changed from £%,.2f to £%,.2f (δ £%,.2f) because %s", player2.getName(), balance1, balance2, delta, reason));

        // Only handle sender if sender is not also the receiver
        if (player1 != player2) {
            // Handles online and offline messages for sender
            Player onlinePlayer1 = player1.getPlayer();
            MailList playerMailList1 = this.app.getMailManager().getMailList(player1);
            String playerMessage1 = String.format("You changed %s's balance from £%,.2f to £%,.2f (δ £%,.2f)", player2.getName(), balance1, balance2, delta);
            if (onlinePlayer1 != null) {
                this.info(onlinePlayer1, playerMessage1);
            } else {
                playerMailList1.createMail(String.format("%s <daysAgo> days ago", playerMessage1));
            }
        }


        // Handles online and offline messages for receiver
        Player onlinePlayer2 = player2.getPlayer();
        MailList playerMailList2 = this.app.getMailManager().getMailList(player2);
        String playerMessage2 = String.format("Your balance changed from £%,.2f to £%,.2f (δ £%,.2f) because %s", balance1, balance2, delta, reason);
        if (onlinePlayer2 != null) {
            this.info(onlinePlayer2, playerMessage2);
        } else {
            playerMailList2.createMail(String.format("%s <daysAgo> days ago", playerMessage2));
        }
    }

    /**
     * Handles Console logging, Player messaging and player mail for the failure of a balance change.
     * Player1 and Player2 can be the same!
     * @param player1 - The command sender (Person who requested balance change)
     * @param player2 - The command receiver (Person who's balance is changing)
     * @param balance1 - The balance before the change.
     * @param balance2 - The balance after the change
     * @param error - The error causing the failure.
     */
    public void logFailedBalance(OfflinePlayer player1, OfflinePlayer player2, double balance1, double balance2, String error) {
        // Round balances
        balance1 = this.app.getEconomyManager().round(balance1);
        balance2 = this.app.getEconomyManager().round(balance2);

        // Get the change
        double delta = this.app.getEconomyManager().round(balance2 - balance1);

        // The message to send
        String playerMessage = String.format("Couldn't change %s's balance from £%,.2f to £%,.2f (δ £%,.2f) because %s", player2.getName(), balance1, balance2, delta, error);

        // Send console log of balance change
        this.warn(playerMessage);

        // Handles online and offline messages for receiver
        Player onlinePlayer1 = player1.getPlayer();
        MailList playerMailList = this.app.getMailManager().getMailList(player1);
        if (onlinePlayer1 != null) {
            // Player is online - send message
            this.warn(onlinePlayer1, playerMessage);
        } else {
            // Player is offline - create mail
            playerMailList.createMail(String.format("%s <daysAgo> days ago", playerMessage));
        }
    }

    /**
     * Handles the console logging, player message and player mail for the purchase of an item
     * @param player - The player
     * @param amount - The amount of the item
     * @param cost - The cost of the items
     * @param materialName - The name of the item
     */
    public void logPurchase(OfflinePlayer player, int amount, double cost, String materialName) {
        //cost = this.app.getEconomyManager().round(cost);
        // Send console log for purchase
        this.info(String.format("%s purchased %d %s for £%,.2f", player.getName(), amount, materialName, cost));

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.app.getMailManager().getMailList(player);
        String player1Message = String.format("Purchased %d %s for £%,.2f", amount, materialName, cost);
        if (onlinePlayer != null) {
            this.info(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <daysAgo> days ago", player1Message));
        }
    }

    /**
     * Handles the console logging, player message and player mail for the failed purchase of an item
     * @param player - The player
     * @param amount - The amount of the item
     * @param cost - The cost of the items
     * @param materialName - The name of the item
     * @param error - The error
     */
    public void logFailedPurchase(OfflinePlayer player, int amount, double cost, String materialName, String error) {
        cost = this.app.getEconomyManager().round(cost);
        // Send console log for failed purchase
        this.warn(String.format("%s couldn't purchase %d %s for £%,.2f because %s", player.getName(), amount, materialName, this.app.getEconomyManager().round(cost), error));

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.app.getMailManager().getMailList(player);
        String player1Message = String.format("Couldn't purchase %d %s for £%,.2f because %s", amount, materialName, cost, error);
        if (onlinePlayer != null) {
            this.warn(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <daysAgo> days ago", player1Message));
        }
    }

    /**
     * Handles the console logging, player message and player mail of the sale of an item
     * @param player - The player
     * @param amount - The amount of items
     * @param value - The value of the items
     * @param materialName - The name of the item
     */
    public void logSale(OfflinePlayer player, int amount, double value, String materialName) {
        value = this.app.getEconomyManager().round(value);
        // Send console log for sale
        this.info(String.format("%s sold %d %s for £%,.2f", player.getName(), amount, materialName, this.app.getEconomyManager().round(value)));

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.app.getMailManager().getMailList(player);
        String player1Message = String.format("Sold %d %s for £%,.2f", amount, materialName, value);
        if (onlinePlayer != null) {
            this.info(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <daysAgo> days ago", player1Message));
        }
    }

    /**
     * Handles the console logging, player message and player mail of the failed sale of an item
     * @param player - The player
     * @param amount - The amount of the item
     * @param value - The value of the items
     * @param materialName - The material name
     * @param error - The error
     */
    public void logFailedSale(OfflinePlayer player, int amount, double value, String materialName, String error) {
        value = this.app.getEconomyManager().round(value);
        // Send console log for failed sale
        this.warn(String.format("%s couldn't sell %d %s for £%,.2f because %s", player.getName(), amount, materialName, this.app.getEconomyManager().round(value), error));

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.app.getMailManager().getMailList(player);
        String player1Message = String.format("Couldn't sell %d %s for £%,.2f because %s", amount, materialName, value, error);
        if (onlinePlayer != null) {
            this.warn(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <daysAgo> days ago", player1Message));
        }
    }
}
