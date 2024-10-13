package org.divinitycraft.divinityeconomy.player;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.utils.LRUCache;
import org.bukkit.OfflinePlayer;

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
    protected Set<OfflinePlayer> load(Object key) {
        return null;
    }
}
