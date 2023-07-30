package me.edgrrrr.de.utils;

import com.google.errorprone.annotations.ForOverride;
import me.edgrrrr.de.DEPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * At face value this is a map for storing loaded objects
 * But what it does is only load a certain maximum number of objects into memory.
 * And when an object whose file is not in memory is loaded, their file is loaded from storage.
 * These are then updated on the fly and it tries to only keep often loaded objects in memory.
 */
public abstract class LRUCache<O, E> extends ConcurrentHashMap<Object, Object> {
    /**
     * Memory size stores the maximum number of users in memory at once.
     * Userfile is the folder where users files are saved.
     * Order List stores the order in which users were requested.
     *   Users towards the back of the list are trimmed from the memory.
     *   Newly made requests place a user at the front of the queue.
     */
    protected final DEPlugin main;
    protected final int memorySize;
    protected final List<Object> orderList;


    public LRUCache(DEPlugin main) {
        this.main = main;
        this.memorySize = Converter.constrainInt(this.loadMemorySize(), 0, Integer.MAX_VALUE);
        this.orderList = Collections.synchronizedList(new ArrayList<>());
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
    private void setFront(Object key) {
        synchronized (this.orderList) {
            this.orderList.remove(key);
            this.orderList.add(0, key);
        }
    }



    /**
     * Trims both the orderlist and map to the size of memorySize
     */
    private void trim() {
        int i = this.orderList.size();
        synchronized(this.orderList) {
            Iterator<Object> iterator = this.orderList.iterator();
            while (iterator.hasNext() && i > this.memorySize) {
                this.remove(iterator.next());
                iterator.remove();
                i--;
            }
        }
    }


    /**
     * Updates the map with the key provided and trims it.
     *
     * @param key
     */
    private void setFrontAndTrim(Object key) {
        this.setFront(key);
        this.trim();
    }


    /**
     * This acts as the original map.get
     */
    @Nullable
    protected Object query(Object key) {
        return super.get(key);
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
     * OVERRIDE!
     * Loads an object from file or creates a new object and places it into memory
     * @param key
     * @return Object
     */
    @ForOverride
    protected Object load(Object key) {
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
    public Object get(Object key) {
        Object result = super.get(key);
        if (result == null) {
            result = this.load(key);
            if (result != null)
                this.put(key, result); // Could be null

            this.getMain().getConsole().debug("Loaded %s from storage", key);
        } else {
            this.getMain().getConsole().debug("Loaded %s from memory", key);
        }

        return result;
    }


    /**
     * OVERRIDE!
     * Puts the given object into memory.
     * @param key
     * @param value
     * @return
     */
    @ForOverride
    @Override
    public Object put(@Nonnull Object key, @Nonnull Object value) {
        Object object = super.put(key, value);
        this.setFrontAndTrim(key);
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
    public Object remove(@Nonnull Object key) {
        synchronized (this.orderList) {
            this.orderList.remove(key);
            return super.remove(key);
        }
    }
}
