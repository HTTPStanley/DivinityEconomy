package me.edgrrrr.de.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class EconomyPlayer {
    private static final BigDecimal MAX_DOUBLE = new BigDecimal(String.valueOf(Double.MAX_VALUE));
    private static final int DECIMAL_SCALE = Double.MAX_EXPONENT;
    private final OfflinePlayer offlinePlayer;
    private final File file;
    private final FileConfiguration playerConfig;

    public EconomyPlayer(OfflinePlayer offlinePlayer, File file, FileConfiguration playerConfig) {
        this.offlinePlayer = offlinePlayer;
        this.file = file;
        this.playerConfig = playerConfig;
    }

    public static boolean canHave(BigDecimal balance, double amount) {
        return balance.add(BigDecimal.valueOf(amount)).compareTo(EconomyPlayer.MAX_DOUBLE) < 0;
    }

    public static String getFilename(OfflinePlayer offlinePlayer) {
        return String.format("%s.yml", offlinePlayer.getUniqueId().toString().toLowerCase());
    }

    public static EconomyPlayer create(OfflinePlayer offlinePlayer, File file, FileConfiguration fileConf, double balance) {
        EconomyPlayer economyPlayer = new EconomyPlayer(offlinePlayer, file, fileConf);
        economyPlayer.set(EconomyFileKeys.BALANCE, BigDecimal.valueOf(balance).toString());
        economyPlayer.set(EconomyFileKeys.UUID, offlinePlayer.getUniqueId().toString());
        economyPlayer.set(EconomyFileKeys.NAME, offlinePlayer.getName());
        economyPlayer.save();

        return economyPlayer;
    }

    public boolean isLegal() {
        return this.offlinePlayer != null;
    }

    public double getBalance() {
        return this._getBalance().doubleValue();
    }

    public void setBalance(double balance) {
        this.set(EconomyFileKeys.BALANCE, BigDecimal.valueOf(balance).toString());
    }

    public void setBalance(BigDecimal balance) {
        this.set(EconomyFileKeys.BALANCE, this.scale(balance).toString());
    }

    public BigDecimal scale(BigDecimal value) {
        return value.setScale(EconomyPlayer.DECIMAL_SCALE - 1, RoundingMode.DOWN).setScale(EconomyPlayer.DECIMAL_SCALE, RoundingMode.DOWN);
    }

    public BigDecimal scale(double value) {
        return this.scale(BigDecimal.valueOf(value));
    }

    private BigDecimal _getBalance() {
        return this.scale(new BigDecimal(this.playerConfig.getString(EconomyFileKeys.BALANCE.key)));
    }

    public double withdraw(double amount) {
        return this._withdraw(amount).doubleValue();
    }

    private BigDecimal _withdraw(double amount) {
        this.setBalance(this._getBalance().subtract(this.scale(amount)));
        return this._getBalance();
    }

    public double deposit(double amount) {
        return this._deposit(amount).doubleValue();
    }

    public BigDecimal _deposit(double amount) {
        this.setBalance(this._getBalance().add(this.scale(amount)));
        return this._getBalance();
    }

    public boolean has(double amount) {
        return this._getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0;
    }

    public boolean canHave(double amount) {
        return EconomyPlayer.canHave(this._getBalance(), amount);
    }

    public void update() {
        this.setName(this.offlinePlayer.getName());
    }

    public String getLastKnownName() {
        return this.playerConfig.getString(EconomyFileKeys.NAME.key);
    }

    public void setName(String name) {
        this.set(EconomyFileKeys.NAME, name);
    }

    public UUID getUUID() {
        return UUID.fromString(this.playerConfig.getString(EconomyFileKeys.UUID.key));
    }

    public void setUUID(UUID uuid) {
        this.set(EconomyFileKeys.UUID, uuid.toString());
    }

    public FileConfiguration getPlayerConfig() {
        return playerConfig;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public String getFilename() {
        return EconomyPlayer.getFilename(this.offlinePlayer);
    }

    public void set(EconomyFileKeys key, Object value) {
        this.playerConfig.set(key.key, value);
        this.save();
    }

    public void save() {
        try {
            this.playerConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
