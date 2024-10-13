package org.divinitycraft.divinityeconomy.console;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.DivinityModule;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.mail.MailList;
import org.divinitycraft.divinityeconomy.utils.Converter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Console class for sending uniform messages to players and the console.
 */
public class Console extends DivinityModule {
    private static final String[] variables = {"<VERSION>"};
    private static final String[] variableValues = {"<VERSION>"};
    private static Console i;
    private final ConsoleCommandSender consoleSender;

    // Settings
    private final boolean debugMode;
    private final String chatPrefix;
    private final String consolePrefix;
    private final int scale;
    private final String currencyPrefix;
    private final String currencySuffix;

    public Console(DEPlugin main) {
        super(main);
        this.consoleSender = main.getServer().getConsoleSender();

        // insert version into variables
        Console.variableValues[0] = main.getDescription().getVersion();

        // Get settings
        FileConfiguration conf = main.getConfig();
        this.debugMode = conf.getBoolean(Setting.CHAT_DEBUG_OUTPUT_BOOLEAN.path);
        String prefix = conf.getString(Setting.CHAT_PREFIX_STRING.path);
        String conPrefix = conf.getString(Setting.CHAT_CONSOLE_PREFIX.path);
        this.chatPrefix = insertColours(prefix);
        conPrefix = insertColours(conPrefix);
        this.consolePrefix = insertVariables(conPrefix);
        this.scale = Converter.constrainInt(conf.getInt(Setting.CHAT_ECONOMY_DIGITS_INT.path), 0, 8);
        this.currencyPrefix = conf.getString(Setting.CHAT_ECONOMY_PREFIX_STRING.path);
        this.currencySuffix = conf.getString(Setting.CHAT_ECONOMY_SUFFIX_STRING.path);

        Console.i = this;
    }

    /**
     * Returns the Console object
     *
     * @return
     */
    public static Console get() {
        return Console.i;
    }


    /**
     * Inserts the variables into the string
     *
     * @param string - The string to insert the variables into
     * @return The string with the variables inserted
     */
    private static String insertVariables(String string) {
        for (int idx = 0; idx < variables.length; idx++) {
            string = string.replace(variables[idx], variableValues[idx]);
        }
        return string;
    }

    /**
     * Inserts the colours into the string
     *
     * @param string - The string to insert the colours into
     * @return The string with the colours inserted
     */
    private static String insertColours(String string) {
        for (ChatColor colour : ChatColor.values()) {
            string = string.replaceAll(String.format("(&%s)|(%s)", colour.getChar(), colour.name()), colour.toString());
        }
        return string;
    }


    /**
     * Initialisation of the object
     */
    @Override
    public void init() {

    }


    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {

    }

    // CONSOLE COMMANDS

    /**
     * Sends a formatted message to the console
     *
     * @param level   - The log level
     * @param message - The message to send
     * @param args    - The arguments
     */
    public void send(LogLevel level, String message, Object... args) {
        this.consoleSender.sendMessage(consolePrefix + level.getColour() + String.format(message, args));
    }


    /**
     * Sends a formatted (default gold) message to the console
     *
     * @param message - The message to send
     * @param args    - The args
     */
    public void migrate(String message, Object... args) {
        this.send(LogLevel.MIGRATE, message, args);
    }


    /**
     * Sends a formatted (default green) message to the console
     *
     * @param message - The message to send
     */
    public void info(String message, Object... args) {
        this.send(LogLevel.INFO, message, args);
    }


    /**
     * Sends a (default green) message to the console
     *
     * @param message - The message to send
     * @param args    - The args
     */
    public void debug(String message, Object... args) {
        if (debugMode) this.send(LogLevel.DEBUG, message, args);
    }


    /**
     * Sends a formatted (default yellow) message to the console
     *
     * @param message - The message to send
     * @param args    - The args
     */
    public void warn(String message, Object... args) {
        this.send(LogLevel.WARNING, message, args);
    }


    /**
     * Sends a formatted (default red) message to the console
     *
     * @param message - The message to send
     * @param args    - The args
     */
    public void severe(String message, Object... args) {
        this.send(LogLevel.SEVERE, message, args);
    }


    // PLAYER

    /**
     * Sends a formatted message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     * @param args    - The args
     */
    public void send(Player player, LogLevel level, String message, Object... args) {
        if (player != null) {
            player.sendMessage(chatPrefix + level.getColour() + String.format(message, args));
        } else {
            this.send(level, message, args);
        }
    }


    /**
     * Sends a usage command to a player
     */
    public void usage(Player player, String errorMessage, String[] usages) {
        this.warn(player, String.format(LangEntry.GENERIC_IncorrectCommandUsage.get(getMain()), errorMessage));
        this.warn(player, String.format(LangEntry.GENERIC_CommandUsage.get(getMain()), Arrays.toString(usages)));
    }


    /**
     * Sends a usage command to the console
     */
    public void usage(String errorMessage, String[] usages) {
        this.warn(String.format(LangEntry.GENERIC_IncorrectCommandUsage.get(getMain()), errorMessage));
        this.warn(String.format(LangEntry.GENERIC_CommandUsage.get(getMain()), Arrays.toString(usages)));
    }


    /**
     * Sends a help message to a player
     *
     * @param player
     * @param command
     * @param description
     * @param usages
     * @param aliases
     */
    public void help(Player player, String command, String description, String[] usages, String[] aliases) {
        this.info(player, LangEntry.GENERIC_HelpFor.get(getMain(), command));
        this.info(player, LangEntry.GENERIC_Description.get(getMain(), description));
        this.info(player, LangEntry.GENERIC_Usages.get(getMain(), ""));
        for (String usage : usages) {
            this.info(player, "  -" + usage);
        }
        this.info(player, LangEntry.GENERIC_Aliases.get(getMain(), String.join(", ", aliases)));
    }


    /**
     * Sends an info message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void info(Player player, String message, Object... args) {
        this.send(player, LogLevel.INFO, message, args);
    }


    /**
     * Sends a warning message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void warn(Player player, String message, Object... args) {
        this.send(player, LogLevel.WARNING, message, args);
    }


    /**
     * Sends a severe message to a player
     *
     * @param player  - The player to send to
     * @param message - The message to send
     */
    public void severe(Player player, String message, Object... args) {
        this.send(player, LogLevel.SEVERE, message, args);
    }


    /**
     * Returns the chat prefix
     *
     * @return
     */
    public String getCurrencyPrefix() {
        return currencyPrefix;
    }


    /**
     * Returns the chat suffix
     *
     * @return
     */
    public String getCurrencySuffix() {
        return currencySuffix;
    }


    /**
     * Converts a double value to a formatted money string
     * @param value
     * @return
     */
    public String formatMoney(double value) {
        return String.format("%s%,." + this.scale +"f%s", this.currencyPrefix, Math.floor(value * Math.pow(10, this.scale)) / Math.pow(10, this.scale), this.currencySuffix);
    }


    /**
     * Returns the formatted balance of a player
     * @param player
     * @return
     */
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
        this.info(getLang().get(LangEntry.TRANSFER_Log), player1.getName(), stringAmount, player2.getName());

        // Handles online & offline messages for sender
        Player onlinePlayer1 = player1.getPlayer();
        MailList player1MailList = this.getMailMan().getMailList(player1.getUniqueId().toString());
        String player1Message = getLang().get(LangEntry.TRANSFER_SourceResponse, stringAmount, player2.getName(), getFormattedBalance(player1));
        if (onlinePlayer1 != null) {
            this.info(onlinePlayer1, player1Message);
        } else {
            player1MailList.createMail(String.format("%s <aptime>", player1Message));
        }

        // Handle online & offline messages for receiver
        Player onlinePlayer2 = player2.getPlayer();
        MailList player2MailList = this.getMailMan().getMailList(player2.getUniqueId().toString());
        String player2Message = getLang().get(LangEntry.TRANSFER_TargetResponse, stringAmount, player1.getName(), getFormattedBalance(player2));
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
        this.warn(getLang().get(LangEntry.TRANSFER_FailedLog), player1.getName(), stringAmount, player2.getName(), error);

        // Handles online and offline messages for sender
        Player onlinePlayer1 = player1.getPlayer();
        MailList player1MailList = this.getMailMan().getMailList(player1.getUniqueId().toString());
        String player1Message = getLang().get(LangEntry.TRANSFER_FailedResponse, stringAmount, player2.getName(), error);
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
        this.info(LangEntry.BALANCE_ChangedLog.get(getMain()), player2.getName(), stringBalance1, stringBalance2, reason);

        // Only handle sender if sender is not also the receiver
        if (player1 != player2) {
            // Handles online and offline messages for sender
            if (player1 != null) {
                Player onlinePlayer1 = player1.getPlayer();
                String playerMessage1 = LangEntry.BALANCE_ChangedSourcePlayer.get(getMain(), player2.getName(), stringBalance1, stringBalance2, reason);
                if (onlinePlayer1 != null) {
                    this.info(onlinePlayer1, playerMessage1);
                }
            }
        }

        // Handles online and offline messages for receiver
        Player onlinePlayer2 = player2.getPlayer();
        MailList playerMailList2 = this.getMailMan().getMailList(player2.getUniqueId().toString());
        String playerMessage2 = LangEntry.BALANCE_ChangedTargetPlayer.get(getMain(), stringBalance1, stringBalance2, reason);
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
        String playerMessage = LangEntry.BALANCE_ChangeFailedLog.get(getMain(), player2.getName(), error);

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
        this.info(getLang().get(LangEntry.PURCHASE_Log), player.getName(), amount, materialName, stringCost);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = getLang().get(LangEntry.PURCHASE_Response, amount, materialName, stringCost);
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
        this.warn(getLang().get(LangEntry.PURCHASE_FailedLog), player.getName(), amount, materialName, error);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = getLang().get(LangEntry.PURCHASE_FailedResponse, amount, materialName, error);
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
        this.info(getLang().get(LangEntry.SALE_Log), player.getName(), amount, materialName, stringValue);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = getLang().get(LangEntry.SALE_Response, amount, materialName, stringValue);
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
        this.warn(getLang().get(LangEntry.SALE_FailedLog), player.getName(), amount, materialName, error);

        // Handles online and offline messages for sender
        Player onlinePlayer = player.getPlayer();
        MailList playerMailList = this.getMailMan().getMailList(player.getUniqueId().toString());
        String player1Message = getLang().get(LangEntry.SALE_FailedResponse, amount, materialName, error);
        if (onlinePlayer != null) {
            this.warn(onlinePlayer, player1Message);
        } else {
            playerMailList.createMail(String.format("%s <aptime>", player1Message));
        }
    }
}
