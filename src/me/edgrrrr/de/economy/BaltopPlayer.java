package me.edgrrrr.de.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BaltopPlayer {
    private final OfflinePlayer offlinePlayer;
    private final double balance;

    BaltopPlayer (OfflinePlayer offlinePlayer, double balance) {
        this.offlinePlayer = offlinePlayer;
        this.balance = balance;
    }

    public double getBalance() {
        return this.balance;
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public String getName() {
        Player player = this.getOfflinePlayer().getPlayer();
        if (player != null) {
            return player.getDisplayName();
        }
        return this.getOfflinePlayer().getName();
    }
}
