package me.edgrrrr.de.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.utils.LRUCache;

import javax.annotation.Nullable;
import java.util.Set;

public class TokenLRUCache extends LRUCache<String, Set<MarketableToken>> {
    public TokenLRUCache(DEPlugin main) {
        super(main);
    }

    @Override
    protected int loadMemorySize() {
        return 512;
    }


    @Override
    protected Object load(Object key) {
        return null;
    }


    @Nullable
    public Set<MarketableToken> getTokens(String key) {
        return (Set<MarketableToken>) this.get(key);
    }
}
