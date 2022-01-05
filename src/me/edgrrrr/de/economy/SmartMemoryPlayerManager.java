package me.edgrrrr.de.economy;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * At face value this is a map for storing loaded players
 * But what it does is only load a certain maximum number of users into memory.
 * And when a user whos file is not in memory is loaded, their file is loaded from storage.
 * These are then updated on the fly and it tries to only keep often loaded players on file.
 */
public class SmartMemoryPlayerManager extends ConcurrentHashMap<Object, EconomyPlayer> {
    /**
     * Memory size stores the maximum number of users in memory at once.
     * Userfile is the folder where users files are saved.
     * Order List stores the order in which users were requested.
     *   Users towards the back of the list are trimmed from the memory.
     *   Newly made requests place a user at the front of the queue.
     */
    private final DEPlugin main;
    private final int memorySize;
    private final File userFile;
    private final List<Object> orderList;


    public SmartMemoryPlayerManager(DEPlugin main, File userFile) {
        this.main = main;
        int memorySize = this.main.getConfMan().getInt(Setting.ECONOMY_SSM_INTEGER);
        if (memorySize < 1) {
            memorySize = 1;
        }
        this.memorySize = memorySize;
        this.userFile = userFile;
        this.orderList = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Sets the front of the order list with the key provided.
     * @param key
     */
    private void setFront(Object key) {
        this.orderList.remove(key);
        this.orderList.add(0, key);
    }

    /**
     * Trims both the orderlist and map to the size of memorySize
     */
    private void trim() {
        int i;
        while((i = this.orderList.size()-1) >= this.memorySize) {
            this.remove(this.orderList.get(i));
            this.orderList.remove(i);
        }
    }

    /**
     * Updates the map with the key provided and trims it.
     * @param key
     */
    private void setFrontAndTrim(Object key) {
        this.setFront(key);
        this.trim();
    }

    /**
     * Loads a player from file or creates a new player and places it into memory
     * @param key
     * @return EconomyPlayer
     */
    private EconomyPlayer load(Object key) {
        File file = new File(this.userFile, EconomyPlayer.getFilename(String.valueOf(key)));

        boolean result;
        if (!file.exists()) {
            result = this.create(file);
        } else {
            result = file.exists();
        }

        EconomyPlayer player = null;
        if (result) {
            player = this.ingest(file);
        }

        if (player != null) {
            this.put(String.valueOf(key), player);
        }

        return player;
    }

    /**
     * Ingests a given file and returns the EconomyPlayer2 it belongs to.
     * !!!This automatically assumes the file exists, do not give it a non-existing file.
     * @param userFile
     * @return EconomyPlayer
     */
    private EconomyPlayer ingest(File userFile) {
        return new EconomyPlayer(userFile, this.main.getConfMan().readFile(userFile));
    }

    /**
     * Attempts to create the user file given and returns if success
     * @param userFile
     * @return boolean
     */
    private boolean create(File userFile) {
        boolean result = false;
        try {
            result = userFile.createNewFile();
        } catch (IOException e) {
            this.main.getConsole().warn("Failed to create player file (%s) because %s", userFile.getAbsolutePath(), e.getMessage());
        }
        return result;
    }

    /**
     * This acts as the original map.get
     */
    @Nullable
    public EconomyPlayer query(Object key) {
        return super.get(key);
    }


    /**
     * Will attempt to return the player in memory
     * If they do not exist in memory, it is loaded into memory
     * If they do not exist in storage, they are created.
     */
    @Override
    @Nullable
    public EconomyPlayer get(Object key) {
        EconomyPlayer result = super.get(key);
        if (result == null) {
            result = this.load(key);
        }

        return result;
    }

    @Override
    public EconomyPlayer put(@Nonnull Object key, @Nonnull EconomyPlayer value) {
        EconomyPlayer player = super.put(key, value);
        this.setFrontAndTrim(key);
        return player;
    }
}
