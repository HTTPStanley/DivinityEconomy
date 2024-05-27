package me.edgrrrr.de.economy;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.economy.players.EconomyPlayer;
import me.edgrrrr.de.lang.LangEntry;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.UUID;


/**
 * The base class for all economy objects (banks, players, etc)
 * They all share the same methods and fields (balance, name, uuid)
 */
public abstract class EconomyObject {
    protected static final BigDecimal MAX_DOUBLE = new BigDecimal(Double.MAX_VALUE);
    protected static final BigDecimal MIN_DOUBLE = new BigDecimal(Double.MIN_VALUE);
    protected static final MathContext ROUNDING_MC = MathContext.DECIMAL64;
    protected DEPlugin main;
    protected final File file;
    protected final File backupFile;
    protected FileConfiguration config;

    /*
     * The constructor for the economy object
     */
    public EconomyObject(DEPlugin main, File file) {
        // Set fields
        this.main = main;
        this.file = file;
        this.backupFile = getBackupFile(file);

        // Load config
        this.loadConfig();
    }

    public DEPlugin getMain() {
        return this.main;
    }


    /**
     * Runs tasks relating to backups
     */
    private void loadConfig() {
        // Check if the file exists and if the backup file doesn't exist
        boolean fileExists = this.fileExists();
        boolean backupFileExists = this.backupFileExists();

        // If neither file exists, create new files
        if (!fileExists && !backupFileExists) {
            try {
                this.file.createNewFile();
                this.backupFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.config = getMain().getConfMan().readFile(this.file);
        }

        // If the file exists
        else if (fileExists) {
            this.config = getMain().getConfMan().readFile(this.file);
        }

        // If the backup file exists and the file doesn't exist
        else {
            this.config = getMain().getConfMan().readFile(this.backupFile);
        }

        // Save the config (Also syncs backup)
        this.save();
    }


    /**
     * Returns the balance of this player
     * @return double
     */
    public double getBalanceAsDouble() {
        return this._getBalance().doubleValue();
    }


    /**
     * Returns the balance of this player
     * @return
     */
    public BigDecimal getBalance() {
        return this._getBalance();
    }


    /**
     * Sets the balance of this player
     * @param balance
     */
    public void setBalance(double balance) {
        this._setBalance(BigDecimal.valueOf(balance));
    }


    /**
     * Sets the balance of this player
     * @param balance
     */
    public void setBalance(BigDecimal balance) {
        this._setBalance(balance);
    }


    /**
     * Withdraws the given amount from the players balance
     * @param amount
     * @return double
     */
    public double withdraw(double amount) {
        return this._withdraw(amount).doubleValue();
    }

    /**
     * Deposits the amount given into the players balance
     * @param amount
     * @return double
     */
    public double deposit(double amount) {
        return this._deposit(amount).doubleValue();
    }


    /**
     * Returns if the player has at-least the amount given
     * @param amount
     * @return boolean
     */
    public boolean has(double amount) {
        return this._getBalance().compareTo(new BigDecimal(String.valueOf(amount))) >= 0;
    }


    /**
     * Returns if the player can be given the amount given
     * @param amount
     * @return boolean
     */
    public boolean canHave(double amount) {
        return EconomyPlayer.canHave(this._getBalance(), amount);
    }


    /**
     * Returns the on-file stored name of the player
     * @return String
     */
    public String getName() {
        return this.getString(FileKey.NAME, null);
    }


    /**
     * Returns the on-file stored uuid of the player
     * @return UUID
     */
    @Nullable
    public UUID getUUID() {
        String uuid = this.getString(FileKey.UUID, null);
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }


    /**
     * Returns the on-file stored uuid of the player
     * @return String
     */
    @Nullable
    public String getUUIDAsString() {
        return this.getString(FileKey.UUID, null);
    }


    /**
     * Returns the file of the player
     * @return File
     */
    @Nullable
    public File getFile() {
        return this.file;
    }


    /**
     * Returns if the file exists
     * @return
     */
    public boolean fileExists() {
        if (this.file == null) {
            return false;
        }
    	return this.file.exists();
    }


    /**
     * Returns the backup file of the player
     * @return File
     */
    @Nullable
    public File getBackupFile() {
        return this.backupFile;
    }


    /**
     * Returns if the backup file exists
     */
    public boolean backupFileExists() {
        if (this.backupFile == null) {
            return false;
        }
        return this.backupFile.exists();
    }


    /**
     * Scales the value given to the standard scaling
     * @param value
     * @return BigDecimal
     */
    private BigDecimal scale(BigDecimal value) {
        return value.round(ROUNDING_MC).stripTrailingZeros();
    }


    /**
     * Scales the value given to the standard scaling
     * @param value
     * @return BigDecimal
     */
    private BigDecimal scale(double value) {
        return this.scale(new BigDecimal(String.valueOf(value)));
    }


    /**
     * INTERNAL FUNCTION
     * Returns the players balance
     * @return BigDecimal
     */
    private BigDecimal _getBalance() {
        return this.scale(this.getDecimal(FileKey.BALANCE));
    }


    /**
     * INTERNAL FUNCTION
     * Sets the players balance
     * @param balance
     * @return
     */
    private void _setBalance(BigDecimal balance) {
        this.setAndSave(FileKey.BALANCE, this.scale(balance).toString());
    }


    /**
     * INTERNAL FUNCTION
     * withdraws the amount given from the players balance
     * @param amount
     * @return BigDecimal
     */
    private BigDecimal _withdraw(double amount) {
        this.setBalance(this._getBalance().subtract(this.scale(amount)));
        return this._getBalance();
    }


    /**
     * INTERNAL FUNCTION
     * Deposits the amount given into the players account
     * @param amount
     * @return BigDecimal
     */
    private BigDecimal _deposit(double amount) {
        this.setBalance(this._getBalance().add(this.scale(amount)));
        return this._getBalance();
    }


    /**
     * Attempts to remove any invalid keys from the players file
     */
    protected void clean() {
        for (String key : this.config.getKeys(false)) {
            if ((key != null) && (!this.checkKey(key))) {
                this.config.set(key, null);
            }
        }
    }


    /**
     * Returns if the given key is valid for this object
     * @param key
     * @return
     */
    protected abstract boolean checkKey(@Nonnull String key);


    /**
     * Parses the given value as a decimal
     * @param value
     * @return
     */
    @Nonnull
    protected BigDecimal parseDecimal(String value) {
        // Try to parse the value
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            getMain().getConsole().warn(LangEntry.ECONOMY_IncorrectlyFormattedBalance.get(getMain()), this.file.getName());
        }

        // Try to parse the value as a double
        try {
            BigDecimal decimal = BigDecimal.valueOf(Double.parseDouble(value));
            getMain().getConsole().info(LangEntry.ECONOMY_RecoveredFile.get(getMain()), this.file.getName());
            return decimal;
        } catch (NumberFormatException e) {
            getMain().getConsole().warn(LangEntry.ECONOMY_FailedToRecoverFile.get(getMain()), this.file.getName());
        }


        // Return 0
        return BigDecimal.ZERO;
    }


    /**
     * Returns the requested key as a BigDecimal value
     * @param key
     * @return
     */
    @Nonnull
    protected BigDecimal getDecimal(@Nonnull FileKey key) {
        return this.parseDecimal(this.getString(key, BigDecimal.ZERO.toString()));
    }


    /**
     * Returns the requested key as a BigDecimal value
     * @param key
     * @param default_
     * @return
     */
    @Nonnull
    protected BigDecimal getDecimal(@Nonnull FileKey key, @Nullable String default_) {
        return this.parseDecimal(this.getString(key, default_));
    }



    /**
     * Returns the requested key, if the key does not exist then it is overwritten with the given default_ value
     * The requested value is then returned nested in String.valueOf
     * @param key
     * @param default_
     * @return String
     */
    @Nullable
    protected String getString(@Nonnull FileKey key, @Nullable String default_) {
        return String.valueOf(this.get(key, default_));
    }


    /**
     * Returns the requested key, if the key does not exist then it is overwritten with the given default_ value
     * @param key
     * @param default_
     * @return
     */
    protected boolean getBoolean(@Nonnull FileKey key, boolean default_) {
        return (boolean) this.get(key, default_);
    }


    /**
     * Returns the requested key, if the key does not exist then it is overwritten with the given default_ value
     * @param key
     * @param default_
     * @return
     */
    @Nullable
    protected Object get(@Nonnull FileKey key, @Nullable Object default_) {
        // Get the value
        Object value = this.config.get(key.getKey());

        // If the value is null then set it to the default_
        if (value == null) {
            value = this.set(key, default_);
        }

        // Return the value
        return value;
    }


    /**
     * Sets the given key to the given value.
     * @param key
     * @param value
     */
    @Nonnull
    protected EconomyObject set(@Nonnull FileKey key, @Nullable Object value) {
        this.config.set(key.getKey(), value);
        return this;
    }


    /**
     * Sets the given key to the given value and saves to disk.
     */
    @Nonnull
    protected EconomyObject setAndSave(@Nonnull FileKey key, @Nullable Object value) {
        this.set(key, value);
        this.save();
        return this;
    }


    /**
     * Saves the config to file.
     */
    protected void save() {
        // Save the file
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Save a backup
        try {
            this.config.save(this.backupFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns the filename for the given uuid
     * @param uuid
     * @return String
     */
    @Nonnull
    public static String getFilename(@Nonnull UUID uuid) {
        return getFilename(uuid.toString());
    }


    /**
     * Returns the filename for the given uuid
     * @param uuid
     * @return String
     */
    @Nonnull
    public static String getFilename(@Nonnull String uuid) {
        return String.format("%s.yml",uuid);
    }


    /**
     * Returns the backup file for the given file
     * @param file
     * @return File
     */
    @Nonnull
    public static File getBackupFile(@Nonnull File file) {
        return new File(file.getParentFile(), file.getName() + ".bak");
    }


    /**
     * Returns if a balance can be deducted by the amount given
     * @param balance
     * @param amount
     * @return
     */
    public static boolean canHave(BigDecimal balance, double amount) {
        return balance.add(BigDecimal.valueOf(amount)).compareTo(MAX_DOUBLE) < 0;
    }
}
