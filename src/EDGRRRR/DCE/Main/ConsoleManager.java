package EDGRRRR.DCE.Main;

import EDGRRRR.DCE.Mail.MailList;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class ConsoleManager {
    private final DCEPlugin app;

    // Settings
    private final boolean debugMode;
    private final ChatColor infoColour;
    private final ChatColor warnColour;
    private final ChatColor severeColour;
    private final ChatColor debugColour;
    private final ChatColor prefixColour;
    private final ChatColor prefixSepColour;
    private final String prefix;
    private final String conPrefix;

    // Colours
    private final HashMap<String, ChatColor> colourMap;

    public ConsoleManager(DCEPlugin app) {
        this.app = app;

        // Colours :D
        this.colourMap = new HashMap<>();
        this.colourMap.put("AQUA", ChatColor.AQUA);
        this.colourMap.put("BLACK", ChatColor.BLACK);
        this.colourMap.put("BLUE", ChatColor.BLUE);
        this.colourMap.put("DARK_AQUA", ChatColor.DARK_AQUA);
        this.colourMap.put("DARK_BLUE", ChatColor.DARK_BLUE);
        this.colourMap.put("DARK_GRAY", ChatColor.DARK_GRAY);
        this.colourMap.put("DARK_GREEN", ChatColor.DARK_GREEN);
        this.colourMap.put("DARK_PURPLE", ChatColor.DARK_PURPLE);
        this.colourMap.put("DARK_RED", ChatColor.DARK_RED);
        this.colourMap.put("GOLD", ChatColor.GOLD);
        this.colourMap.put("GRAY", ChatColor.GRAY);
        this.colourMap.put("GREEN", ChatColor.GREEN);
        this.colourMap.put("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE);
        this.colourMap.put("MAGIC", ChatColor.MAGIC);
        this.colourMap.put("RED", ChatColor.RED);
        this.colourMap.put("WHITE", ChatColor.WHITE);
        this.colourMap.put("YELLOW", ChatColor.YELLOW);


        // Get settings
        this.debugMode = (this.app.getConfig().getBoolean(this.app.getConfigManager().strChatDebug));
        this.infoColour = this.getColour(this.app.getConfigManager().strChatInfClr);
        this.warnColour = this.getColour(this.app.getConfigManager().strChatWrnClr);
        this.severeColour = this.getColour(this.app.getConfigManager().strChatSvrClr);
        this.debugColour = this.getColour(this.app.getConfigManager().strChatDbgClr);
        this.prefixColour = this.getColour(this.app.getConfigManager().strChatPfxClr);
        this.prefixSepColour = this.getColour(this.app.getConfigManager().strChatPfxSepClr);
        String prefix = this.app.getConfig().getString(this.app.getConfigManager().strChatMsgPfx);
        String conPrefix = this.app.getConfig().getString(this.app.getConfigManager().strChatConsPfx).replace("%V", this.app.getDescription().getVersion());
        String prefixSep = this.app.getConfig().getString(this.app.getConfigManager().strChatPfxSep);
        this.prefix = prefixColour + prefix + prefixSepColour + prefixSep;
        this.conPrefix = prefixColour + conPrefix + prefixSepColour + prefixSep;
    }

    /**
     * Resolves string colours to the corresponding ChatColor object
     *
     * @param optionName - The config option name
     * @return ChatColour - The colour
     */
    private ChatColor getColour(String optionName) {
        String option = this.app.getConfig().getString(optionName);
        String defaultOption = this.app.getConfig().getDefaults().getString(optionName);
        ChatColor colour = this.colourMap.get(option);
        if (colour == null) {
            colour = this.colourMap.get(defaultOption);
        }

        return colour;
    }

    // CONSOLE COMMANDS

    /**
     * Sends a message to the console
     *
     * @param message - The message to send
     */
    private void send(String message) {
        this.app.getServer().getConsoleSender().sendMessage(conPrefix + message);
    }

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void info(String message) {
        this.send(infoColour + message);
    }

    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     */
    public void debug(String message) {
        if (debugMode) this.send(debugColour + message);
    }

    /**
     * Sends a (default yellow) message to the console
     *
     * @param message - The message to send
     */
    public void warn(String message) {
        this.send(warnColour + message);
    }

    /**
     * Sends a (default red) message to the console
     *
     * @param message - The message to send
     */
    public void severe(String message) {
        this.send(severeColour + message);
    }

    // PLAYER

    /**
     * Sends a message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    private void send(Player player, String message) {
        player.sendMessage(prefix + message);
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
        this.send(player, infoColour + message);
    }

    /**
     * Sends a warning message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void warn(Player player, String message) {
        this.send(player, warnColour + message);
    }

    /**
     * Sends a severe message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void severe(Player player, String message) {
        this.send(player, severeColour + message);
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
        Player onlinePlayer2 = player1.getPlayer();
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
