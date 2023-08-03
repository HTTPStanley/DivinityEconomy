package me.edgrrrr.de.economy;

import org.bukkit.OfflinePlayer;

public class BaltopPlayer {
    private final OfflinePlayer offlinePlayer;
    private final double balance;
    private final NameStore nameStore;

    BaltopPlayer (OfflinePlayer offlinePlayer, double balance, NameStore nameStore) {
        this.offlinePlayer = offlinePlayer;
        this.balance = balance;
        this.nameStore = nameStore;
    }

    public double getBalance() {
        return this.balance;
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public String getName() {
        return this.nameStore.name();
    }
}
