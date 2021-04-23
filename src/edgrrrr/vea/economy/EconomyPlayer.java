package edgrrrr.vea.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class EconomyPlayer {
    private final OfflinePlayer offlinePlayer;
    private final File file;
    private final FileConfiguration playerConfig;

    private static final BigDecimal MAX_DOUBLE = new BigDecimal(String.valueOf(Double.MAX_VALUE));

    public EconomyPlayer(OfflinePlayer offlinePlayer, File file, FileConfiguration playerConfig) {
        this.offlinePlayer = offlinePlayer;
        this.file = file;
        this.playerConfig = playerConfig;
    }

    private BigDecimal getNearestValue(BigDecimal value, double direction) {
        double closestValue = value.doubleValue();
        if (BigDecimal.valueOf(closestValue).compareTo(value) > 0){
            closestValue = Math.nextAfter(closestValue, direction);
        }
        return BigDecimal.valueOf(closestValue);
    }

    private BigDecimal getNearestValueDown(BigDecimal value) {
        return this.getNearestValue(value, Math.floor(value.doubleValue()));
    }

    private BigDecimal getNearestValueUp(BigDecimal value) {
        return this.getNearestValue(value, Math.ceil(value.doubleValue()));
    }

    public boolean isLegal() {
        return this.offlinePlayer != null;
    }

    public double getBalance() {
        return this._getBalance().doubleValue();
    }

    private BigDecimal _getBalance() {
        BigDecimal value = new BigDecimal(this.playerConfig.getString(EconomyFileKeys.BALANCE.key));
        return this.getNearestValueDown(value);
    }

    public double withdraw(double amount) {
        return this._withdraw(amount).doubleValue();
    }

    private BigDecimal _withdraw(double amount) {
        BigDecimal newAmount = this._getBalance().subtract(BigDecimal.valueOf(amount));
        this.setBalance(this.getNearestValueDown(newAmount).doubleValue());
        return this._getBalance();
    }

    public double deposit(double amount) {
        return this._deposit(amount).doubleValue();
    }

    public BigDecimal _deposit(double amount) {
        BigDecimal newAmount = this._getBalance().add(BigDecimal.valueOf(amount));
        this.setBalance(this.getNearestValueDown(newAmount).doubleValue());
        return this._getBalance();
    }

    public boolean has(double amount) {
        return this._getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0;
    }

    public boolean canHave(double amount) {
        return EconomyPlayer.canHave(this._getBalance(), amount);
    }

    public static boolean canHave(double balance, double amount) {
        return EconomyPlayer.canHave(BigDecimal.valueOf(balance), amount);
    }

    public static boolean canHave(BigDecimal balance, double amount) {
        return balance.add(BigDecimal.valueOf(amount)).compareTo(EconomyPlayer.MAX_DOUBLE) < 0;
    }

    public void update() {
        this.setName(this.offlinePlayer.getName());
    }

    public void setBalance(double balance) {
        this.set(EconomyFileKeys.BALANCE, BigDecimal.valueOf(balance).toString());
    }

    public void setBalance(BigDecimal balance) {
        this.set(EconomyFileKeys.BALANCE, balance.toString());
    }

    public String getLastKnownName() {
        return this.playerConfig.getString(EconomyFileKeys.NAME.key);
    }

    public void setName(String name) {
        this.set(EconomyFileKeys.NAME, name);
    }

    public UUID getUUID() {
        return UUID.fromString(Objects.requireNonNull(this.playerConfig.getString(EconomyFileKeys.UUID.key)));
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

    public static String getFilename(OfflinePlayer offlinePlayer) {
        return String.format("%s.yml", offlinePlayer.getUniqueId().toString().toLowerCase());
    }

    public static EconomyPlayer create(OfflinePlayer offlinePlayer, File file, FileConfiguration fileConf, double balance) {
        EconomyPlayer economyPlayer = new EconomyPlayer(offlinePlayer, file, fileConf);
        economyPlayer.set(EconomyFileKeys.BALANCE, balance);
        economyPlayer.set(EconomyFileKeys.UUID, offlinePlayer.getUniqueId().toString());
        economyPlayer.set(EconomyFileKeys.NAME, offlinePlayer.getName());
        economyPlayer.save();

        return economyPlayer;
    }
}
