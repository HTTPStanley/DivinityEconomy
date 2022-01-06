package me.edgrrrr.de.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class EconomyPlayer {
    private static final BigDecimal MAX_DOUBLE = new BigDecimal(String.valueOf(Double.MAX_VALUE));
    private static final int DECIMAL_SCALE = Double.MAX_EXPONENT;
    public static int maxLogs = 500;
    private final File file;
    private final FileConfiguration playerConfig;

    public EconomyPlayer(File file, FileConfiguration playerConfig) {
        this.file = file;
        this.playerConfig = playerConfig;
    }

    /**
     * Returns the balance of this player
     * @return double
     */
    public double getBalance() {
        return this._getBalance().doubleValue();
    }

    /**
     * Sets the balance of this player
     * @param balance
     */
    public void setBalance(double balance) {
        this.set(EconomyFileKeys.BALANCE, BigDecimal.valueOf(balance).toString());
    }

    /**
     * Sets the balance of this player
     * @param balance
     */
    public void setBalance(BigDecimal balance) {
        this.set(EconomyFileKeys.BALANCE, this.scale(balance).toString());
    }

    /**
     * Scales the value given to the standard scaling
     * @param value
     * @return BigDecimal
     */
    private BigDecimal scale(BigDecimal value) {
        return value.setScale(EconomyPlayer.DECIMAL_SCALE - 1, RoundingMode.DOWN).setScale(EconomyPlayer.DECIMAL_SCALE, RoundingMode.DOWN);
    }

    /**
     * Scales the value given to the standard scaling
     * @param value
     * @return BigDecimal
     */
    private BigDecimal scale(double value) {
        return this.scale(BigDecimal.valueOf(value));
    }

    /**
     * INTERNAL FUNCTION
     * Returns the players balance
     * @return BigDecimal
     */
    private BigDecimal _getBalance() {
        return this.scale(new BigDecimal(this.playerConfig.getString(EconomyFileKeys.BALANCE.key)));
    }

    /**
     * Withdraws the given amount from the players balance
     * @param amount
     * @return double
     */
    public double withdraw(double amount) {
        return this._withdraw(amount).doubleValue();
    }

    /**
     * INTERNAL FUNCTION
     * withdraws the amount given from the players balance
     * @param amount
     * @return BigDecimal
     */
    private BigDecimal _withdraw(double amount) {
        this.setBalance(this._getBalance().subtract(this.scale(amount)));
        return this._getBalance();
    }

    /**
     * Deposits the amount given into the players balance
     * @param amount
     * @return double
     */
    public double deposit(double amount) {
        return this._deposit(amount).doubleValue();
    }

    /**
     * INTERNAL FUNCTION
     * Deposits the amount given into the players account
     * @param amount
     * @return BigDecimal
     */
    public BigDecimal _deposit(double amount) {
        this.setBalance(this._getBalance().add(this.scale(amount)));
        return this._getBalance();
    }

    /**
     * Returns if the player has at-least the amount given
     * @param amount
     * @return boolean
     */
    public boolean has(double amount) {
        return this._getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0;
    }

    /**
     * Returns if the player can be given the amount given
     * @param amount
     * @return boolean
     */
    public boolean canHave(double amount) {
        return EconomyPlayer.canHave(this._getBalance(), amount);
    }

    /**
     * Returns the on-file stored name of the player
     * @return String
     */
    public String getName() {
        return (String) this.get(EconomyFileKeys.NAME, "PLAYER-HAS-NO-SET-NAME");
    }

    /**
     * Returns the on-file stored uuid of the player
     * @return String
     */
    @Nullable
    public String getUUID() {
        return (String) this.get(EconomyFileKeys.UUID, null);
    }

    /**
     * Returns the file of the player
     * @return File
     */
    @Nullable
    public File getFile() {
        return this.file;
    }

    public String getFileID() {
        return this.file.getName().replace(".yml", "");
    }

    /**
     * Ensures that the given player is up to date.
     * This is also used to validate newly created players which may have 0 keys.
     * @param player
     * @param s
     * @return EconomyPlayer2
     */
    public EconomyPlayer update(OfflinePlayer player, String s) {
        if (player != null) {
            this.get(EconomyFileKeys.NAME, player.getName());
            this.get(EconomyFileKeys.UUID, player.getUniqueId().toString());
        } else if (s != null) {
            this.get(EconomyFileKeys.NAME, s);
        }

        this.get(EconomyFileKeys.BALANCE, 0);

        return this;
    }

    /**
     * Returns the requested key, if the key does not exist then it is overwritten with the given default_ value
     * @param key
     * @param default_
     * @return
     */
    public Object get(EconomyFileKeys key, Object default_) {
        Object value = this.playerConfig.get(key.key);
        if (value == null) {
            value = this.set(key, default_);
        }

        return value;
    }

    /**
     * Sets the given key to the given value and saves to disk.
     * @param key
     * @param value
     */
    public Object set(EconomyFileKeys key, Object value) {
        this.playerConfig.set(key.key, value);

        // TODO: LOGGING
        List<String> logs = null;
        if (maxLogs > 0) {
            logs = this.playerConfig.getStringList(EconomyFileKeys.LOGS.key);
            logs.add(0, String.format("[%s] Key %s SET %s", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()), key, value));
            logs.subList(Math.min(maxLogs-1, logs.size()-1), Math.min(logs.size()-1, maxLogs-1)).clear();
        }
        this.playerConfig.set(EconomyFileKeys.LOGS.key, logs);
        // TODO: END LOGGING

        this.save();
        return value;
    }

    /**
     * Saves the playerConfig to file.
     */
    public void save() {
        try {
            this.playerConfig.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns if a balance can be deducted by the amount given
     * @param balance
     * @param amount
     * @return
     */
    public static boolean canHave(BigDecimal balance, double amount) {
        return balance.add(BigDecimal.valueOf(amount)).compareTo(EconomyPlayer.MAX_DOUBLE) < 0;
    }

    /**
     * Returns the filename for the given player
     * @param offlinePlayer
     * @return String
     */
    public static String getFilename(OfflinePlayer offlinePlayer) {
        return getFilename(offlinePlayer.getUniqueId());
    }

    /**
     * Returns the filename for the given uuid
     * @param uuid
     * @return String
     */
    public static String getFilename(UUID uuid) {
        return getFilename(uuid.toString());
    }

    /**
     * Returns the filename for the given uuid
     * @param uuid
     * @return String
     */
    public static String getFilename(String uuid) {
        return String.format("%s.yml",uuid);
    }
}
