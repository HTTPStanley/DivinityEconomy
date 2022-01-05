package me.edgrrrr.de.console;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.mail.MailList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EconConsole extends Console {

    private int scale;
    private String currencyPrefix;
    private String currencySuffix;

    public EconConsole(DEPlugin app) {
        super(app);
    }

    @Override
    public void init() {
        this.scale = this.getConfMan().getInt(Setting.CHAT_ECONOMY_DIGITS_INT);
        this.currencyPrefix = this.getConfMan().getString(Setting.CHAT_ECONOMY_PREFIX_STRING);
        this.currencySuffix = this.getConfMan().getString(Setting.CHAT_ECONOMY_SUFFIX_STRING);
    }

    public String formatMoney(double value) {
        return String.format("%s%,." + this.scale + "f%s", this.currencyPrefix, value, this.currencySuffix);
    }

    public String getFormattedBalance(OfflinePlayer player) {
        return this.formatMoney(this.getEconMan().getBalance(player));
    }

    /**
     * Handles console logger, player messages and player mail for the completion of a transfer
     *
     * @param player1 - The sender
     * @param player2 - The receiver
     * @param amount  - The amount sent
     */
    public void logTransfer(OfflinePlayer player1, OfflinePlayer player2, double amount) {
        String stringAmount = this.formatMoney(amount);

        // Send console log of transaction
        this.info("%s sent %s to %s", player1.getName(), stringAmount, player2.getName());

        // Handles online & offline messages for sender
        Player onlinePlayer1 = player1.getPlayer();
        MailList player1MailList = this.getMailMan().getMailList(player1.getUniqueId().toString());
        String player1Message = String.format("Sent %s to %s", stringAmount, player2.getName());
        if (onlinePlayer1 != null) {
            this.info(onlinePlayer1, player1Message);
        } else {
            player1MailList.createMail(String.format("%s <aptime>", player1Message));
        }

        // Handle online & offline messages for receiver
        Player onlinePlayer2 = player2.getPlayer();
        MailList player2MailList = this.getMailMan().getMailList(player2.getUniqueId().toString());
        String player2Message = String.format("Received %s from %s", stringAmount, player1.getName());
        if (onlinePlayer2 != null) {
            this.info(onlinePlayer2, player2Message);
        } else {
            player2MailList.createMail(String.format("%s <aptime>", player2Message));
        }
    }

    /**
     * Handles console logging, player messages and player mail for the failure of a transfer
     *
     * @param player1 - The sender
     * @param player2 - The receiver
     * @param amount  - The amount sent
     * @param error   - The error
     */
    public void logFailedTransfer(OfflinePlayer player1, OfflinePlayer player2, double amount, String error) {
        String stringAmount = this.formatMoney(amount);
        // Send console log of transaction
        this.warn("%s couldn't send %s to %s because %s", player1.getName(), stringAmount, player2.getName(), error);

        // Handles online and offline messages for sender
        Player onlinePlayer1 = player1.getPlayer();
        MailList player1MailList = this.getMailMan().getMailList(player1.getUniqueId().toString());
        String player1Message = String.format("Couldn't send %s to %s because %s", stringAmount, player2.getName(), error);
        if (onlinePlayer1 != null) {
            this.warn(onlinePlayer1, player1Message);
        } else {
            player1MailList.createMail(player1Message);
        }
    }

    /**
     * Handles console logging, player messaging and player mail for the successful changing of a players balance.
     * Player1 and Player 2 can be the same!
     *
     * @param player1  - The command sender
     * @param player2  - The command receiver
     * @param balance1 - The balance before the change
     * @param balance2 - The balance after the change
     * @param reason   - The reason for the change.
     */
    public void logBalance(@Nullable OfflinePlayer player1, @Nonnull OfflinePlayer player2, double balance1, double balance2, String reason) {
        String stringBalance1 = this.formatMoney(balance1);
        String stringBalance2 = this.formatMoney(balance2);

        // Send console log of balance change
        this.info("%s's balance changed from %s to %s because %s", player2.getName(), stringBalance1, stringBalance2, reason);

        // Only handle sender if sender is not also the receiver
        if (player1 != player2) {
            // Handles online and offline messages for sender
            if (player1 != null) {
                Player onlinePlayer1 = player1.getPlayer();
                String playerMessage1 = String.format("You changed %s's balance from %s to %s", player2.getName(), stringBalance1, stringBalance2);
                if (onlinePlayer1 != null) {
                    this.info(onlinePlayer1, playerMessage1);
                }
            }
        }

        // Handles online and offline messages for receiver
        Player onlinePlayer2 = player2.getPlayer();
        MailList playerMailList2 = this.getMailMan().getMailList(player2.getUniqueId().toString());
        String playerMessage2 = String.format("Your balance changed from %s to %s because %s", stringBalance1, stringBalance2, reason);
        if (onlinePlayer2 != null) {
            this.info(onlinePlayer2, playerMessage2);
        } else {
            playerMailList2.createMail(String.format("%s <aptime>", playerMessage2));
        }
    }

    /**
     * Handles Console logging, Player messaging and player mail for the failure of a balance change.
     * Player1 and Player2 can be the same!
     *
     * @param player1 - The command sender (Person who requested balance change)
     * @param player2 - The command receiver (Person who's balance is changing)
     * @param error   - The error causing the failure.
     */
    public void logFailedBalance(@Nullable OfflinePlayer player1, @Nonnull OfflinePlayer player2, String error) {
        // The message to send
        String playerMessage = String.format("Couldn't change %s's balance because %s", player2.getName(), error);

        // Send console log of balance change
        this.warn(playerMessage);

        if (player1 != player2) {
            // Handles online and offline messages for sender
            if (player1 != null) {
                Player onlinePlayer1 = player1.getPlayer();
                if (onlinePlayer1 != null) {
                    // Player is online - send message
                    this.warn(onlinePlayer1, playerMessage);
                }
            }
        }

        // Handles online and offline messages for receiver#
        Player onlinePlayer2 = player2.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player2.getUniqueId().toString());
        if (onlinePlayer2 != null) {
            // Player is online - send message
            this.warn(onlinePlayer2, playerMessage);
        } else {
            // Player is offline - create mail
            playerMailList.createMail(String.format("%s <aptime>", playerMessage));
        }
    }

    /**
     * Handles the console logging, player message and player mail for the purchase of an item
     *
     * @param player       - The player
     * @param amount       - The amount of the item
     * @param cost         - The cost of the items
     * @param materialName - The name of the item
     */
    public void logPurchase(OfflinePlayer player, int amount, double cost, String materialName) {
        String stringCost = this.formatMoney(cost);

        // Send console log for purchase
        this.info("%s purchased %d %s for %s", player.getName(), amount, materialName, stringCost);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = String.format("Purchased %d %s for %s", amount, materialName, stringCost);
        if (onlinePlayer != null) {
            this.info(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <aptime>", player1Message));
        }
    }

    /**
     * Handles the console logging, player message and player mail for the failed purchase of an item
     *
     * @param player       - The player
     * @param amount       - The amount of the item
     * @param materialName - The name of the item
     * @param error        - The error
     */
    public void logFailedPurchase(OfflinePlayer player, int amount, String materialName, String error) {
        this.warn("%s couldn't purchase %d %s because %s", player.getName(), amount, materialName, error);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = String.format("Couldn't purchase %d %s because %s", amount, materialName, error);
        if (onlinePlayer != null) {
            this.warn(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <aptime>", player1Message));
        }
    }

    /**
     * Handles the console logging, player message and player mail of the sale of an item
     *
     * @param player       - The player
     * @param amount       - The amount of items
     * @param value        - The value of the items
     * @param materialName - The name of the item
     */
    public void logSale(OfflinePlayer player, int amount, double value, String materialName) {
        String stringValue = this.formatMoney(value);
        // Send console log for sale
        this.info("%s sold %d %s for %s", player.getName(), amount, materialName, stringValue);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = String.format("Sold %d %s for %s", amount, materialName, stringValue);
        if (onlinePlayer != null) {
            this.info(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <aptime>", player1Message));
        }
    }

    /**
     * Handles the console logging, player message and player mail of the failed sale of an item
     *
     * @param player       - The player
     * @param amount       - The amount of the item
     * @param materialName - The material name
     * @param error        - The error
     */
    public void logFailedSale(OfflinePlayer player, int amount, String materialName, String error) {
        this.warn("%s couldn't sell %d %s because %s", player.getName(), amount, materialName, error);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = String.format("Couldn't sell %d %s because %s", amount, materialName, error);
        if (onlinePlayer != null) {
            this.warn(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <aptime>", player1Message));
        }
    }
}
