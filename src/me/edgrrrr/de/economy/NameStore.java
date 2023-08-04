package me.edgrrrr.de.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class NameStore {
    private final String name;
    private final UUID uuid;
    private final String displayName;
    private final String no_name_value;

    public NameStore(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        this.displayName = onlinePlayer != null ? onlinePlayer.getDisplayName() : null;
        this.no_name_value = player.getUniqueId().toString();
    }

    @Nonnull
    public String name() {
        return displayName != null ? displayName : name != null ? name : this.no_name_value;
    }

    @Nonnull
    public String getName() {
        return name != null ? name : this.no_name_value;
    }

    @Nonnull
    public UUID getUuid() {
        return uuid;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }
}
