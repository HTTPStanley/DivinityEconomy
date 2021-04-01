package edgrrrr.vea.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EconomyPlayer {
    private final OfflinePlayer offlinePlayer;
    private final File file;
    private final FileConfiguration playerConfig;

    public EconomyPlayer(OfflinePlayer offlinePlayer, File file, FileConfiguration playerConfig) {
        this.offlinePlayer = offlinePlayer;
        this.file = file;
        this.playerConfig = playerConfig;
    }

    public boolean isLegal() {
        return this.offlinePlayer != null;
    }

    public double getBalance() {
        return this.playerConfig.getDouble(EconomyFileKeys.BALANCE.get());
    }

    public double withdraw(double amount) {
        this.setBalance(this.getBalance() - amount);
        return this.getBalance();
    }

    public double deposit(double amount) {
        this.setBalance(this.getBalance() + amount);
        return this.getBalance();
    }

    public boolean has(double amount) {
        return this.getBalance() >= amount;
    }

    public boolean canHave(double amount) {
        double balance = this.getBalance();
        return balance + amount != balance;
    }

    public void update() {
        if (!this.getLastKnownName().equals(this.offlinePlayer.getName())) this.setName(this.offlinePlayer.getName());
    }

    public void setBalance(double balance) {
        this.set(EconomyFileKeys.BALANCE, balance);
    }

    public String getLastKnownName() {
        return this.playerConfig.getString(EconomyFileKeys.NAME.get());
    }

    public void setName(String name) {
        this.set(EconomyFileKeys.NAME, name);
    }

    public UUID getUUID() {
        return UUID.fromString(this.playerConfig.getString(EconomyFileKeys.UUID.get()));
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
        this.playerConfig.set(key.get(), value);
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
