package me.edgrrrr.de.utils;

import com.google.errorprone.annotations.ForOverride;
import me.edgrrrr.de.DEPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * At face value this is a map for storing loaded objects
 * But what it does is only load a certain maximum number of objects into memory.
 * And when an object whose file is not in memory is loaded, their file is loaded from storage.
 * These are then updated on the fly and it tries to only keep often loaded objects in memory.
 */
public abstract class LRUCache<K, V> extends ConcurrentHashMap<K, V> {
    /**
     * Memory size stores the maximum number of users in memory at once.
     * Userfile is the folder where users files are saved.
     * Order List stores the order in which users were requested.
     *   Users towards the back of the list are trimmed from the memory.
     *   Newly made requests place a user at the front of the queue.
     */
    protected final DEPlugin main;
    protected final int memorySize;
    protected final LinkedBlockingDeque<K> orderList;
    private final static double DEBLOAT_FACTOR = 0.75;


    public LRUCache(DEPlugin main) {
        this.main = main;

        // Load sensible memory size
        int memorySize = this.loadMemorySize();
        if (memorySize <= 0) {
            this.memorySize = 0;
        } else {
            this.memorySize = Converter.constrainInt(memorySize, 64, Integer.MAX_VALUE);
        }

        // Load sensible order list
        this.orderList = new LinkedBlockingDeque<>(this.loadMemorySize());
    }


    /**
     * Returns the main plugin
     * @return DEPlugin
     */
    protected DEPlugin getMain() {
        return this.main;
    }



    /**
     * Sets the front of the order list with the key provided.
     * @param key
     */
    private void setFront(K key) {
        synchronized (this.orderList) {
            // Remove last element if the list is too big
            if (this.orderList.size() >= this.memorySize) {
                this.trim();
            }

            // Remove any duplicates
            this.orderList.addFirst(key);
        }
    }


    /**
     * Trims the memory.
     */
    private void trim() {
        synchronized (this.orderList) {
            int debloatAmount = this.getDebloatSize();
            getMain().getConsole().debug("Debloating %s objects", debloatAmount);
            while (debloatAmount > 0) {
                Object obj = this.orderList.removeLast();
                if (!this.orderList.contains(obj)) {
                    super.remove(obj);
                    debloatAmount--;
                }
            }
        }
    }


    /**
     * OVERRIDE! this function to return the desired memory size.
     *
     * @return int
     */
    @ForOverride
    protected int loadMemorySize() {
        return 0;
    }

    /**
     * Returns the debloat size
     * @return int
     */
    protected int getDebloatSize() {
        if (this.memorySize <= 0)
            return 0;

        return (int) Math.ceil(this.memorySize * DEBLOAT_FACTOR);
    }


    protected boolean query(K key) {
        return this.containsKey(key);
    }


    /**
     * OVERRIDE!
     * Loads an object from file or creates a new object
     * @param key
     * @return Object
     */
    @ForOverride
    protected V load(K key) {
        return null;
    }



    /**
     * OVERRIDE!
     * Will attempt to return the object in memory
     * If it does not exist in memory, it is loaded into memory
     * If it does not exist in storage, they are created.
     */
    @ForOverride
    @Override
    @Nullable
    public V get(Object k) {
        K key = (K) k;
        V result = (V) super.get(key);
        if (result == null) {
            result = this.load(key);
            if (result != null) {
                this.put(key, result); // Could be null
                getMain().getConsole().debug("Loaded %s from storage", key);
            }
        } else {
            getMain().getConsole().debug("Loaded %s from memory", key);
        }

        return result;
    }


    /**
     * OVERRIDE!
     * Puts the given object into memory.
     * @param k
     * @param v
     * @return
     */
    @ForOverride
    @Override
    public V put(@Nonnull Object k, @Nonnull Object v) {
        K key = (K) k;
        V value = (V) v;
        // If memory size is 0, then we don't need to worry about memory.
        if (this.memorySize == 0)
            return load(key);

        // Get the object from memory
        V object = (V) super.put(key, value);
        this.setFront(key);
        return object;
    }


    @Override
    public void clear() {
        synchronized (this.orderList) {
            super.clear();
            this.orderList.clear();
        }
    }


    @Override
    public V remove(@Nonnull Object k) {
        K key = (K) k;
        synchronized (this.orderList) {
            this.orderList.remove(key);
            return (V) super.remove(key);
        }
    }
}
