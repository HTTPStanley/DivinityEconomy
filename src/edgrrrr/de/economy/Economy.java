package edgrrrr.de.economy;

import edgrrrr.de.config.ConfigManager;
import edgrrrr.de.console.EconConsole;
import edgrrrr.de.events.PlayerJoin;
import edgrrrr.de.player.PlayerManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Economy implements net.milkbowl.vault.economy.Economy {
    private final JavaPlugin app;
    private final ConfigManager configManager;
    private final EconConsole console;
    private final PlayerManager playerManager;
    private final int fractionalDigits;
    private final String currencyNamePlural;
    private final String currencyNameSingular;
    private final HashMap<UUID, EconomyPlayer> economyPlayerMap;
    private final File userFolder;

    private static final String foldername = "userdata";

    public Economy(JavaPlugin app,
                   ConfigManager configManager,
                   EconConsole console,
                   PlayerManager playerManager,
                   int fractionalDigits,
                   String currencyNamePlural,
                   String currencyNameSingular) {

        this.app = app;
        this.configManager = configManager;
        this.console = console;
        this.playerManager = playerManager;
        this.fractionalDigits = fractionalDigits;
        this.currencyNamePlural = currencyNamePlural;
        this.currencyNameSingular = currencyNameSingular;
        this.economyPlayerMap = new HashMap<>();
        this.userFolder = this.configManager.getFolder(Economy.foldername);

        this.registerPlayers();
        this.app.getServer().getPluginManager().registerEvents(new PlayerJoin(this), this.app);
        this.console.info(String.format("Loaded %d players", this.economyPlayerMap.size()));
    }

    private void registerPlayers() {
        File[] files = Objects.requireNonNull(this.userFolder.listFiles());
        if (files.length > 0) {
            for (File file : files) {
                if (!file.isFile()) continue;

                try {
                    this.addPlayer(file);
                } catch (Exception e) {
                    this.console.warn(String.format("Player (%s) could not be registered from file because %s.", file, e.getMessage()));
                }
            }
        }
    }

    private void addPlayer(File file) {
        FileConfiguration fileConf = this.configManager.readFile(file);
        UUID playerUUID = UUID.fromString(Objects.requireNonNull(fileConf.getString(EconomyFileKeys.UUID.key)));
        OfflinePlayer offlinePlayer = this.app.getServer().getOfflinePlayer(playerUUID);
        EconomyPlayer player = new EconomyPlayer(offlinePlayer, file, fileConf);

        if (player.isLegal()) {
            player.update();
            this.economyPlayerMap.put(playerUUID, player);
        }
    }

    @Nullable
    private EconomyPlayer get(UUID uuid) {
        EconomyPlayer economyPlayer = this.economyPlayerMap.get(uuid);
        // If the player has uuid, they must have joined before.
        // allowFetch = false | for this reason
        // could return null still, at which point there's a bug elsewhere.
        if (economyPlayer == null) {
            OfflinePlayer player = this.playerManager.getOfflinePlayer(uuid, false);
            this.createPlayerAccount(player);
        }

        return this.economyPlayerMap.get(uuid);
    }

    @Nullable
    private EconomyPlayer get(String playerName) {
        EconomyPlayer economyPlayer = null;
        playerName = playerName.toLowerCase();
        for (EconomyPlayer player : this.economyPlayerMap.values()) {
            if (player.getLastKnownName().toLowerCase().equals(playerName)) {
                economyPlayer = player;
                break;
            }
        }

        return economyPlayer;
    }

    /**
     * Checks if economy method is enabled.
     *
     * @return Success or Failure
     */
    @Override
    public boolean isEnabled() {
        return this.economyPlayerMap != null;
    }

    /**
     * Gets name of economy method
     *
     * @return Name of Economy Method
     */
    @Override
    public String getName() {
        return this.app.getName();
    }

    //TODO
    /**
     * Returns true if the given implementation supports banks.
     *
     * @return true if the implementation supports banks
     */
    @Override
    public boolean hasBankSupport() {
        return false;
    }

    /**
     * Some economy plugins round off after a certain number of digits.
     * This function returns the number of digits the plugin keeps
     * or -1 if no rounding occurs.
     *
     * @return number of digits after the decimal point kept
     */
    @Override
    public int fractionalDigits() {
        return this.fractionalDigits;
    }

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.
     *
     * @param amount to format
     * @return Human readable string describing amount
     */
    @Override
    public String format(double amount) {
        return String.format("%,." + this.fractionalDigits + "%", amount);
    }

    /**
     * Returns the name of the currency in plural form.
     * If the economy being used does not support currency names then an empty string will be returned.
     *
     * @return name of the currency (plural)
     */
    @Override
    public String currencyNamePlural() {
        return this.currencyNamePlural;
    }

    /**
     * Returns the name of the currency in singular form.
     * If the economy being used does not support currency names then an empty string will be returned.
     *
     * @return name of the currency (singular)
     */
    @Override
    public String currencyNameSingular() {
        return this.currencyNameSingular;
    }

    /**
     * @param playerName
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)} instead.
     */
    @Override
    public boolean hasAccount(String playerName) {
        return this.get(playerName) != null;
    }

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     *
     * @param player to check
     * @return if the player has an account
     */
    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return this.economyPlayerMap.containsKey(player.getUniqueId());
    }

    /**
     * @param playerName
     * @param worldName
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)} instead.
     */
    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    /**
     * Checks if this player has an account on the server yet on the given world
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     *
     * @param player    to check in the world
     * @param worldName world-specific account
     * @return if the player has an account
     */
    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return this.hasAccount(player);
    }

    /**
     * @param playerName
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)} instead.
     */
    @Override
    public double getBalance(String playerName) {
        EconomyPlayer player = this.get(playerName);
        if (player == null) {
            return 0;
        } else {
            return player.getBalance();
        }
    }

    /**
     * Gets balance of a player
     *
     * @param player of the player
     * @return Amount currently held in players account
     */
    @Override
    public double getBalance(OfflinePlayer player) {
        return this.get(player.getUniqueId()).getBalance();
    }

    /**
     * @param playerName
     * @param world
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)} instead.
     */
    @Override
    public double getBalance(String playerName, String world) {
        return this.getBalance(playerName);
    }

    /**
     * Gets balance of a player on the specified world.
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     *
     * @param player to check
     * @param world  name of the world
     * @return Amount currently held in players account
     */
    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return this.get(player.getUniqueId()).getBalance();
    }

    /**
     * @param playerName
     * @param amount
     * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)} instead.
     */
    @Override
    public boolean has(String playerName, double amount) {
        EconomyPlayer player = this.get(playerName);
        if (player != null) return player.has(amount);
        else return false;
    }

    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to check
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return this.get(player.getUniqueId()).has(amount);
    }

    /**
     * @param playerName
     * @param worldName
     * @param amount
     * @deprecated As of VaultAPI 1.4 use @{link {@link #has(OfflinePlayer, String, double)} instead.
     */
    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return this.has(playerName, amount);
    }

    /**
     * Checks if the player account has the amount in a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     *
     * @param player    to check
     * @param worldName to check with
     * @param amount    to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return this.has(player, amount);
    }

    /**
     * @param playerName
     * @param amount
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        EconomyPlayer player = this.get(playerName);
        if (player == null) return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "unknown player");
        return this.withdrawPlayer(player.getOfflinePlayer(), amount);
    }
    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to withdraw from
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        EconomyPlayer economyPlayer = this.get(player.getUniqueId());

        if (amount < 0) return new EconomyResponse(amount, economyPlayer.getBalance(), EconomyResponse.ResponseType.FAILURE, "negative amounts are not allowed");

        if (!economyPlayer.has(amount)) return new EconomyResponse(amount, economyPlayer.getBalance(), EconomyResponse.ResponseType.FAILURE, "withdrawal would lead to overdraft");

        double balance = economyPlayer.withdraw(amount);
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * @param playerName
     * @param worldName
     * @param amount
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return this.withdrawPlayer(playerName, amount);
    }

    /**
     * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     *
     * @param player    to withdraw from
     * @param worldName - name of the world
     * @param amount    Amount to withdraw
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.withdrawPlayer(player, amount);
    }

    /**
     * @param playerName
     * @param amount
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        EconomyPlayer player = this.get(playerName);
        if (player == null) return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "unknown player");
        return this.depositPlayer(player.getOfflinePlayer(), amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        EconomyPlayer economyPlayer = this.get(player.getUniqueId());

        if (amount < 0) return new EconomyResponse(amount, economyPlayer.getBalance(), EconomyResponse.ResponseType.FAILURE, "negative amounts are not allowed");

        if (!economyPlayer.canHave(amount)) return new EconomyResponse(amount, economyPlayer.getBalance(), EconomyResponse.ResponseType.FAILURE, "balance may be too large");

        double balance = economyPlayer.deposit(amount);
        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * @param playerName
     * @param worldName
     * @param amount
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return this.depositPlayer(playerName, amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balance will be returned.
     *
     * @param player    to deposit to
     * @param worldName name of the world
     * @param amount    Amount to deposit
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return this.depositPlayer(player, amount);
    }

    //TODO
    /**
     * @param name
     * @param player
     * @deprecated As of VaultAPI 1.4 use {{@link #createBank(String, OfflinePlayer)} instead.
     */
    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    //TODO
    /**
     * Creates a bank account with the specified name and the player as the owner
     *
     * @param name   of account
     * @param player the account should be linked to
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    //TODO
    /**
     * Deletes a bank account with the specified name.
     *
     * @param name of the back to delete
     * @return if the operation completed successfully
     */
    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    //TODO
    /**
     * Returns the amount the bank has
     *
     * @param name of the account
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    //TODO
    /**
     * Returns true or false whether the bank has the amount specified - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name   of the account
     * @param amount to check for
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    //TODO
    /**
     * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name   of the account
     * @param amount to withdraw
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    //TODO
    /**
     * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name   of the account
     * @param amount to deposit
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    //TODO
    /**
     * @param name
     * @param playerName
     * @deprecated As of VaultAPI 1.4 use {{@link #isBankOwner(String, OfflinePlayer)} instead.
     */
    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    //TODO
    /**
     * Check if a player is the owner of a bank account
     *
     * @param name   of the account
     * @param player to check for ownership
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    //TODO
    /**
     * @param name
     * @param playerName
     * @deprecated As of VaultAPI 1.4 use {{@link #isBankMember(String, OfflinePlayer)} instead.
     */
    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    //TODO
    /**
     * Check if the player is a member of the bank account
     *
     * @param name   of the account
     * @param player to check membership
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    //TODO
    /**
     * Gets the list of banks
     *
     * @return the List of Banks
     */
    @Override
    public List<String> getBanks() {
        return null;
    }

    /**
     * @param playerName
     * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer)} instead.
     */
    @Override
    public boolean createPlayerAccount(String playerName) {
        OfflinePlayer player = this.playerManager.getOfflinePlayer(playerName, false);
        if (player == null) return false;
        else return this.createPlayerAccount(player);
    }

    /**
     * Attempts to create a player account for the given player
     *
     * @param player OfflinePlayer
     * @return if the account creation was successful
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        File file = this.configManager.getFile(this.userFolder, EconomyPlayer.getFilename(player));
        FileConfiguration fileConf = this.configManager.readFile(file);
        EconomyPlayer.create(player, file, fileConf, 0.0);
        this.addPlayer(file);

        return this.hasAccount(player);
    }

    /**
     * @param playerName
     * @param worldName
     * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
     */
    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return this.createPlayerAccount(playerName);
    }

    /**
     * Attempts to create a player account for the given player on the specified world
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this then false will always be returned.
     *
     * @param player    OfflinePlayer
     * @param worldName String name of the world
     * @return if the account creation was successful
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return this.createPlayerAccount(player);
    }
}
