package me.edgrrrr.de.player;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.utils.LRUCache;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.util.Set;

public class PlayerLRUCache extends LRUCache<Object, Set<OfflinePlayer>> {
    /**
     * Constructor
     * @param main
     */
    public PlayerLRUCache(DEPlugin main) {
        super(main);
    }

    @Override
    protected int loadMemorySize() {
        return 10240;
    }


    @Override
    protected Object load(Object key) {
        return null;
    }


    @Nullable
    public Set<OfflinePlayer> getPlayers(String key) {
        return (Set<OfflinePlayer>) this.get(key);
    }
}
