package me.edgrrrr.de.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.utils.LRUCache;

import java.util.Set;

public class TokenLRUCache extends LRUCache<String, Set<String>> {
    public TokenLRUCache(DEPlugin main) {
        super(main);
    }

    @Override
    protected int loadMemorySize() {
        return 1024;
    }


    @Override
    protected Set<String> load(String key) {
        return null;
    }
}
