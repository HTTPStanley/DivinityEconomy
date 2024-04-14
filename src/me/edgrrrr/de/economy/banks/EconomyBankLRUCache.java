package me.edgrrrr.de.economy.banks;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.utils.LRUCache;
import me.edgrrrr.de.lang.LangEntry;

import java.io.File;
import java.io.IOException;

public class EconomyBankLRUCache extends LRUCache<String, EconomyBank> {
    protected final File bankFile;

    public EconomyBankLRUCache(DEPlugin main, File bankFile) {
        super(main);
        this.bankFile = bankFile;
    }


    /**
     * Returns the memory size for this manager
     *
     * @return int
     */
    @Override
    protected int loadMemorySize() {
        return 2048;
    }


    @Override
    public boolean query(String key) {
        return this.containsKey(key) || this.getFile(key).exists();
    }


    public File getFile(String key) {
        return new File(this.bankFile, EconomyBank.getFilename(key));
    }


    /**
     * Loads a bank from file or creates a new bank and places it into memory
     *
     * @param key
     * @return EconomyPlayer
     */
    @Override
    protected EconomyBank load(String key) {
        File file = this.getFile(key);
        return this.ingest(file);
    }


    /**
     * Ingests a given file and returns the EconomyBank it belongs to.
     * !!!This automatically assumes the file exists, do not give it a non-existing file.
     *
     * @param bankFile
     * @return EconomyBank
     */
    protected EconomyBank ingest(File bankFile) {
        return new EconomyBank(getMain(), bankFile);
    }


    /**
     * Attempts to create the user file given and returns if success
     *
     * @param bankFile
     * @return boolean
     */
    protected boolean create(File bankFile) {
        boolean result = false;
        try {
            result = bankFile.createNewFile();
        } catch (IOException e) {
            this.main.getConsole().warn(LangEntry.ECONOMY_FailedToCreateBankFile.get(getMain()), bankFile.getAbsolutePath(), e.getMessage());
        }
        return result;
    }
}