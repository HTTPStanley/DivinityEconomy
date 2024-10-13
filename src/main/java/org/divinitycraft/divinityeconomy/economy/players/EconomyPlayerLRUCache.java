package org.divinitycraft.divinityeconomy.economy.players;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.economy.banks.EconomyBank;
import org.divinitycraft.divinityeconomy.utils.LRUCache;
import org.bukkit.OfflinePlayer;

import java.io.File;

public class EconomyPlayerLRUCache extends LRUCache<OfflinePlayer, EconomyPlayer> {
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


    @Override
    protected boolean query(OfflinePlayer player) {
        return this.containsKey(player) || this.getFile(player).exists();
    }


    protected File getFile(OfflinePlayer offlinePlayer) {
        return new File(this.userFile, EconomyBank.getFilename(offlinePlayer.getUniqueId().toString()));
    }


    /**
     * Loads a bank from file or creates a new bank and places it into memory
     *
     * @param player
     * @return EconomyPlayer
     */
    @Override
    protected EconomyPlayer load(OfflinePlayer player) {
        File file = this.getFile(player);
        return this.ingest(file, player);
    }



    /**
     * Ingests a given file and returns the EconomyPlayer it belongs to.
     * !!!This automatically assumes the file exists, do not give it a non-existing file.
     * @param userFolder
     * @return EconomyPlayer
     */
    protected EconomyPlayer ingest(File userFolder, OfflinePlayer player) {
        return new EconomyPlayer(getMain(), userFolder, player);
    }
}
