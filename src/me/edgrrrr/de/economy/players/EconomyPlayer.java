package me.edgrrrr.de.economy.players;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.economy.EconomyObject;
import me.edgrrrr.de.economy.FileKey;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Set;

public class EconomyPlayer extends EconomyObject {
    private static final Set<FileKey> keys = Set.of(FileKey.NAME, FileKey.UUID, FileKey.BALANCE);
    private final OfflinePlayer player;

    public EconomyPlayer(DEPlugin main, File file, OfflinePlayer player) {
        super(main, file);
        this.player = player;
        this.get(FileKey.NAME, player.getName());
        this.get(FileKey.UUID, player.getUniqueId().toString());
        this.get(FileKey.BALANCE, this.getBalance().toString());
        this.get(FileKey.ENABLE_NOTIFICATIONS, true);
        this.clean();
        this.save();
    }


    /**
     * Returns the player
     * @return
     */
    public OfflinePlayer getPlayer() {
        return this.player;
    }


    /**
     * Returns the player's name
     * @return
     */
    public String getName() {
        return (String) this.get(FileKey.NAME, getMain().getPlayMan().getPlayerName(this.player).name());
    }


    /**
     * Return's the player's notification status
     * @return
     */
    public boolean getNotification() {
        return getBoolean(FileKey.ENABLE_NOTIFICATIONS, true);
    }


    /**
     * Returns the player's balance
     * @return
     */
    public void setNotification(boolean enable) {
        this.set(FileKey.ENABLE_NOTIFICATIONS, enable);
    }


    /**
     * Checks if the given key is valid
     * @param key
     * @return
     */
    @Override
    public boolean checkKey(@Nonnull String key) {
        return keys.contains(FileKey.get(key));
    }
}
