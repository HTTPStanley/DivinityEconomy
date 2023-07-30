package me.edgrrrr.de.economy;

import me.edgrrrr.de.DEPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class EconomyPlayerLRUCache extends me.edgrrrr.de.utils.LRUCache<UUID, EconomyPlayer> {
    protected final File userFile;

    public EconomyPlayerLRUCache(DEPlugin main, File userFile) {
        super(main);
        this.userFile = userFile;
    }


    /**
     * Returns the memory size for this manager
     * @return int
     */
    @Override
    protected int loadMemorySize() {
        return 2048;
    }


    @Nullable
    @Override
    protected EconomyPlayer query(Object key) {
        return (EconomyPlayer) super.query(key);
    }

    /**
     * Loads a player from file or creates a new player and places it into memory
     * @param key
     * @return EconomyPlayer
     */
    @Override
    protected EconomyPlayer load(Object key) {
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

        return player;
    }



    /**
     * Ingests a given file and returns the EconomyPlayer it belongs to.
     * !!!This automatically assumes the file exists, do not give it a non-existing file.
     * @param userFile
     * @return EconomyPlayer
     */
    protected EconomyPlayer ingest(File userFile) {
        return new EconomyPlayer(userFile, this.main.getConfMan().readFile(userFile));
    }



    /**
     * Attempts to create the user file given and returns if success
     * @param userFile
     * @return boolean
     */
    protected boolean create(File userFile) {
        boolean result = false;
        try {
            result = userFile.createNewFile();
        } catch (IOException e) {
            this.main.getConsole().warn("Failed to create player file (%s) because %s", userFile.getAbsolutePath(), e.getMessage());
        }
        return result;
    }


    /**
     *
     * @param key the key whose associated value is to be returned
     * @return EconomyPlayer | null
     */
    @Override
    @Nullable
    public EconomyPlayer get(Object key) {
        return (EconomyPlayer) super.get(key);
    }


    /**
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return EconomyPlayer
     */
    @Override
    public EconomyPlayer put(@Nonnull Object key, @Nonnull Object value) {
        return (EconomyPlayer) super.put(key, value);
    }
}
