package me.edgrrrr.de.economy;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.economy.banks.EconomyBank;
import me.edgrrrr.de.economy.banks.EconomyBankLRUCache;
import me.edgrrrr.de.economy.events.PlayerJoin;
import me.edgrrrr.de.economy.players.EconomyPlayer;
import me.edgrrrr.de.economy.players.EconomyPlayerLRUCache;
import me.edgrrrr.de.utils.Converter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class DivinityEconomy implements net.milkbowl.vault.economy.Economy{
    private static final String userdata = "userdata";
    private static final String bankdata = "bankdata";
    private static final int LIST_TASK_INTERVAL = Converter.getTicks(60);
    private final DEPlugin main;
    private final int fractionalDigits;
    private final String currencyNamePlural;
    private final String currencyNameSingular;
    private final EconomyPlayerLRUCache economyPlayerMap;
    private final EconomyBankLRUCache economyBankMap;
    private final File userFolder;
    private final File bankData;
    private final Set<String> banks;
    private final BukkitRunnable bankListTask = new BukkitRunnable() {
        @Override
        public void run() {
            fetchBanks();
        }
    };

    public DivinityEconomy(DEPlugin main) {
        this.main = main;
        this.fractionalDigits = this.main.getConfMan().getInt(Setting.CHAT_ECONOMY_DIGITS_INT);
        this.currencyNamePlural = this.main.getConfMan().getString(Setting.CHAT_ECONOMY_PLURAL_STRING);
        this.currencyNameSingular = this.main.getConfMan().getString(Setting.CHAT_ECONOMY_SINGULAR_STRING);
        this.userFolder = this.main.getConfMan().getFolder(DivinityEconomy.userdata);
        this.bankData = this.main.getConfMan().getFolder(DivinityEconomy.bankdata);
        this.economyPlayerMap = new EconomyPlayerLRUCache(this.main, userFolder);
        this.economyBankMap = new EconomyBankLRUCache(this.main, bankData);
        this.banks = Collections.synchronizedSet(new HashSet<>());
        this.main.getServer().getPluginManager().registerEvents(new PlayerJoin(this), this.main);
    }

    // QUERY CODE
    public void startTasks() {
        this.bankListTask.runTaskTimerAsynchronously(this.main, 0L, LIST_TASK_INTERVAL);
        fetchBanks();
    }

    public void stopTasks() {
        this.bankListTask.cancel();
    }


    /**
     * Returns an economy player by uuid
     * @param uuid
     * @return
     */
    private EconomyPlayer get(UUID uuid) {
        OfflinePlayer player = this.main.getPlayMan().getPlayer(uuid);
        return this.get(player);
    }


    /**
     * Returns an economy player by offline player
     * @param offlinePlayer
     * @return
     */
    public EconomyPlayer get(@Nonnull OfflinePlayer offlinePlayer) {
        return this.economyPlayerMap.get(offlinePlayer);
    }


    /**
     * Attempts to find a player by name
     * @param name
     * @return
     */
    @Nullable
    public EconomyPlayer get(String name) {
        OfflinePlayer player = this.main.getPlayMan().getPlayer(name, true);
        if (player == null) {
            return null;
        }

        return this.get(player);
    }

    /**
     * Returns an economy bank by name
     */
    public EconomyBank getBank(String name) {
        return this.economyBankMap.get(name);
    }


    // DIVINITY ECONOMY CODE
    /**
     * Returns an economy response detailing the result of the withdraw request
     * @param p - the player
     * @param v - value
     * @return EconomyResponse
     */
    public EconomyResponse withdrawPlayer(EconomyPlayer p, double v) {
        if (v < 0) {
            return new EconomyResponse(v, p.getBalanceAsDouble(), EconomyResponse.ResponseType.FAILURE, "negative amounts are not allowed");
        }

        if (!p.has(v)) {
            return new EconomyResponse(v, p.getBalanceAsDouble(), EconomyResponse.ResponseType.FAILURE, "withdrawal would lead to overdraft");
        }

        double balance = p.withdraw(v);
        return new EconomyResponse(v, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    /**
     * Returns an economy response detailing the result of the deposit request
     * @param p
     * @param v
     * @return
     */
    public EconomyResponse depositPlayer(EconomyPlayer p, double v) {
        if (v < 0)
            return new EconomyResponse(v, p.getBalanceAsDouble(), EconomyResponse.ResponseType.FAILURE, "negative amounts are not allowed");

        if (!p.canHave(v))
            return new EconomyResponse(v, p.getBalanceAsDouble(), EconomyResponse.ResponseType.FAILURE, "balance may be too large");

        double balance = p.deposit(v);
        return new EconomyResponse(v, balance, EconomyResponse.ResponseType.SUCCESS, "");
    }


    // VAULT CODE

    /**
     * Returns if this is enabled
     * @return always true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the name of this provider
     * @return The name of the plugin
     */
    @Override
    public String getName() {
        return this.main.getName();
    }

    /**
     * Returns if the provider has bank support
     * @return always true
     */
    @Override
    public boolean hasBankSupport() {
        return true;
    }

    /**
     * Returns the fractional digits
     * @return int
     */
    @Override
    public int fractionalDigits() {
        return this.fractionalDigits;
    }

    /**
     * Returns the double given string formatted
     * @param v
     * @return %,.<fractionDigits>f
     */
    @Override
    public String format(double v) {
        return this.main.getConsole().formatMoney(v);
    }

    /**
     * Returns the plural currency name
     * @return String
     */
    @Override
    public String currencyNamePlural() {
        return this.currencyNamePlural;
    }

    /**
     * Returns the singular currency name
     * @return String
     */
    @Override
    public String currencyNameSingular() {
        return this.currencyNameSingular;
    }

    /**
     * Returns if the user given has an account
     * @param s - Player name
     * @return boolean
     */
    @Override
    @Deprecated
    public boolean hasAccount(String s) {
        return this.get(s) != null;
    }

    /**
     * Returns if the user given has an account
     * @param offlinePlayer
     * @return boolean
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return this.get(offlinePlayer) != null;
    }

    /**
     * Returns if the user given has an account in the world given
     * @param s - The player name
     * @param s1 - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @return boolean
     */
    @Override
    @Deprecated
    public boolean hasAccount(String s, String s1) {
        return this.hasAccount(s);
    }

    /**
     * Returns if the user given has an account in the world given
     * @param offlinePlayer
     * @param s - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @return boolean
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return this.hasAccount(offlinePlayer);
    }

    /**
     * Returns the balance of the player given
     * @param s - The player name
     * @return double
     */
    @Override
    @Deprecated
    public double getBalance(String s) {
        EconomyPlayer player = this.get(s);
        if (player == null) {
            return 0;
        }
        return player.getBalanceAsDouble();
    }

    /**
     * Returns the balance of the player given
     * @param offlinePlayer
     * @return double
     */
    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return this.get(offlinePlayer).getBalanceAsDouble();
    }

    /**
     * Returns the balance of the player given in the world given
     * @param s - The player name
     * @param s1 - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @return double
     */
    @Override
    @Deprecated
    public double getBalance(String s, String s1) {
        return this.getBalance(s);
    }

    /**
     * Returns the balance of the player given in the world given
     * @param offlinePlayer
     * @param s - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @return double
     */
    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return this.getBalance(offlinePlayer);
    }

    /**
     * Returns if the player given at-least has the amount given
     * @param s - player name
     * @param v - value
     * @return boolean
     */
    @Override
    @Deprecated
    public boolean has(String s, double v) {
        return this.get(s).has(v);
    }

    /**
     * Returns if the player given at-least has the amount given
     * @param offlinePlayer
     * @param v - value
     * @return boolean
     */
    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return this.get(offlinePlayer).has(v);
    }

    /**
     * Returns if the playerr given at-least has the amount given in the world given
     * @param s - The player name
     * @param s1 - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @param v - value
     * @return boolean
     */
    @Override
    @Deprecated
    public boolean has(String s, String s1, double v) {
        return this.has(s, v);
    }

    /**
     * Returns if the player given at-least has the amount given in the world given
     * @param offlinePlayer
     * @param s - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @param v - value
     * @return boolean
     */
    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return this.has(offlinePlayer, v);
    }

    /**
     * Returns an economy response detailing the result of the withdraw request
     * @param s - The player name
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String s, double v) {
        return withdrawPlayer(this.get(s), v);
    }

    /**
     * Returns an economy response detailing the result of the withdraw request
     * @param offlinePlayer
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return withdrawPlayer(this.get(offlinePlayer), v);
    }

    /**
     * Returns an economy response detailing the result of the withdraw request
     * @param s - The player name
     * @param s1 - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return this.withdrawPlayer(s, v);
    }

    /**
     * Returns an economy response detailing the result of the withdraw request
     * @param offlinePlayer
     * @param s - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.withdrawPlayer(offlinePlayer, v);
    }

    /**
     * Returns an economy response detailing the result of the deposit request
     * @param s - The player name
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String s, double v) {
        return this.depositPlayer(this.get(s), v);
    }

    /**
     * Returns an economy response detailing the result of the deposit request
     * @param offlinePlayer
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        return this.depositPlayer(this.get(offlinePlayer), v);
    }

    /**
     * Returns an economy response detailing the result of the deposit request
     * @param s - The player name
     * @param s1 - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return this.depositPlayer(this.get(s), v);
    }

    /**
     * Returns an economy response detailing the result of the deposit request
     * @param offlinePlayer
     * @param s - The world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @param v - value
     * @return EconomyResponse
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return this.depositPlayer(this.get(offlinePlayer), v);
    }


    // BANKS

    /**
     * Loads bank files and adds to internal store
     */
    public void fetchBanks() {
        // Get files
        File[] files = this.bankData.listFiles();
        if (files == null) {
            return;
        }

        // Clear banks
        synchronized (this.banks) {
            this.banks.clear();

            // Loop through files
            for (File file : files) {
                // Load bank
                EconomyBank bank = new EconomyBank(this.main, file);

                // Check if bank is valid
                String name = bank.getName();
                if (name == null || name.isEmpty()) {
                    continue;
                }

                // Add bank
                this.banks.add(name);
            }
        }
    }


    /**
     * @param bankName
     * @param playerName
     */
    @Override
    @Deprecated
    public EconomyResponse createBank(String bankName, String playerName) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Get player
        OfflinePlayer player = this.main.getPlayMan().getPlayer(playerName, true);

        // Check if player exists
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "player does not exist");
        }

        return this.createBank(bankName, player);
    }

    @Override
    public EconomyResponse createBank(String bankName, OfflinePlayer player) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank already exists");
        }

        // Create bank (Get should automatically create the bank)
        EconomyBank bank = this.economyBankMap.get(bankName);
        bank.set(FileKey.NAME, bankName);
        bank.set(FileKey.UUID, player.getUniqueId().toString());
        bank.set(FileKey.MEMBERS, new ArrayList<>(Collections.singleton(player.getUniqueId())));
        bank.setBalance(0.0);
        bank.clean();
        bank.save();
        return new EconomyResponse(0, bank.getBalanceAsDouble(), EconomyResponse.ResponseType.SUCCESS, String.format("Successfully created %s.", bank.getName()));
    }

    @Override
    public EconomyResponse deleteBank(String bankName) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }
        
        if (!this.economyBankMap.containsKey(bankName)) {
            this.economyBankMap.get(bankName);
        }
        // Delete bank
        EconomyBank bank = this.economyBankMap.remove(bankName);
        bank.delete();
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, String.format("Successfully deleted %s.", bankName));
    }

    @Override
    public EconomyResponse bankBalance(String bankName) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }

        // Get bank
        EconomyBank bank = this.economyBankMap.get(bankName);

        // Return balance
        return new EconomyResponse(0, bank.getBalanceAsDouble(), EconomyResponse.ResponseType.SUCCESS, String.format("Successfully retrieved %s's balance.", bank.getName()));
    }

    @Override
    public EconomyResponse bankHas(String bankName, double v) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }

        // Get bank
        EconomyBank bank = this.economyBankMap.get(bankName);

        // Return balance
        boolean has = bank.has(v);
        return new EconomyResponse(0, bank.getBalanceAsDouble(), has ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, String.format("Successfully checked if %s has %s.", bank.getName(), v));
    }

    @Override
    public EconomyResponse bankWithdraw(String bankName, double v) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }

        // Get bank
        EconomyBank bank = this.economyBankMap.get(bankName);

        // Return balance
        double balance = bank.withdraw(v);
        return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, String.format("Successfully withdrew %s from %s.", v, bank.getName()));
    }

    @Override
    public EconomyResponse bankDeposit(String bankName, double v) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }

        // Get bank
        EconomyBank bank = this.economyBankMap.get(bankName);

        // Return balance
        double balance = bank.deposit(v);
        return new EconomyResponse(0, balance, EconomyResponse.ResponseType.SUCCESS, String.format("Successfully deposited %s into %s.", v, bank.getName()));

    }

    /**
     * @param bankName
     * @param playerName
     */
    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String bankName, String playerName) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }

        // Get bank
        EconomyBank bank = this.economyBankMap.get(bankName);

        // Check if player is owner
        boolean isOwner = bank.isOwner(playerName);
        return new EconomyResponse(0, bank.getBalanceAsDouble(), isOwner ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, String.format("Successfully checked if %s is owner of %s.", playerName, bank.getName()));
    }

    @Override
    public EconomyResponse isBankOwner(String bankName, OfflinePlayer offlinePlayer) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }

        // Get bank
        EconomyBank bank = this.economyBankMap.get(bankName);

        // Check if player is owner
        boolean isOwner = bank.isOwner(offlinePlayer.getUniqueId());
        return new EconomyResponse(0, bank.getBalanceAsDouble(), isOwner ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, String.format("Successfully checked if %s is owner of %s.", offlinePlayer.getName(), bank.getName()));
    }

    /**
     * Returns if the given player is a member of the given bank.
     * @param bankName
     * @param playerName
     */
    @Override
    @Deprecated
    public EconomyResponse isBankMember(String bankName, String playerName) {
        // Clean bank name
        bankName = cleanBankName(bankName);

        // Check if bank exists
        if (!this.economyBankMap.query(bankName)) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "bank does not exist");
        }

        // Get player
        OfflinePlayer player = this.main.getPlayMan().getPlayer(playerName, true);

        // Check if player is member
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "player does not exist");
        }

        // Check if player is member
        return this.isBankMember(bankName, player);
    }

    /**
     * Returns if the given player is a member of the given bank.
     * @param bankName
     * @param offlinePlayer
     * @return
     */
    @Override
    public EconomyResponse isBankMember(String bankName, OfflinePlayer offlinePlayer) {
        EconomyPlayer player = this.get(offlinePlayer.getUniqueId());
        EconomyBank bank = this.economyBankMap.get(bankName);

        // Check if player is member
        UUID uuid = player.getUUID();
        if (uuid == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "player does not exist");
        }

        // Check if player is member
        boolean isMember = bank.isMember(uuid);

        // Return response
        return new EconomyResponse(0, bank.getBalanceAsDouble(), isMember ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE, String.format("Successfully checked if %s is member of %s.", player.getName(), bank.getName()));

    }

    /**
     * Returns a list of banks
     * @return
     */
    @Override
    public List<String> getBanks() {
        return new ArrayList<>(this.banks);
    }

    /**
     * Creates a player account
     * @param s - the player name
     * @return boolean
     */
    @Override
    @Deprecated
    public boolean createPlayerAccount(String s) {
        return this.get(s) != null;
    }

    /**
     * Creates a player account
     * @param offlinePlayer
     * @return boolean
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return this.get(offlinePlayer) != null;
    }

    /**
     * Creates a player account
     * @param s - the player name
     * @param s1 - the world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @return boolean
     */
    @Override
    @Deprecated
    public boolean createPlayerAccount(String s, String s1) {
        return this.get(s) != null;
    }

    /**
     * Creates a player account
     * @param offlinePlayer
     * @param s - the world (THIS IS NOT SUPPORTED AND WILL BE IGNORED)
     * @return boolean
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return this.get(offlinePlayer) != null;
    }


    /**
     * Returns a cleaned bank name
     * @param name
     * @return
     */
    public static String cleanBankName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "").strip();
    }
}
