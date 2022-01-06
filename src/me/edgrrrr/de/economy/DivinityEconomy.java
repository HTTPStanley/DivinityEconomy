package me.edgrrrr.de.economy;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.events.PlayerJoin;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DivinityEconomy implements net.milkbowl.vault.economy.Economy {
    private static final String userdata = "userdata";
    private static final String bankdata = "bankdata";
    private final DEPlugin main;
    private final int fractionalDigits;
    private final String currencyNamePlural;
    private final String currencyNameSingular;
    private final SmartMemoryPlayerManager economyPlayerMap;
    private final Map<String, String> userNameMap;
    private final File userFolder;
    private final File bankData;

    public DivinityEconomy(DEPlugin main) {
        this.main = main;
        this.fractionalDigits = this.main.getConfMan().getInt(Setting.CHAT_ECONOMY_DIGITS_INT);
        this.currencyNamePlural = this.main.getConfMan().getString(Setting.CHAT_ECONOMY_PLURAL_STRING);
        this.currencyNameSingular = this.main.getConfMan().getString(Setting.CHAT_ECONOMY_SINGULAR_STRING);
        EconomyPlayer.maxLogs = this.main.getConfMan().getInt(Setting.ECONOMY_MAX_LOGS_INTEGER);
        this.userFolder = this.main.getConfMan().getFolder(DivinityEconomy.userdata);
        this.bankData = this.main.getConfMan().getFolder(DivinityEconomy.bankdata);
        this.economyPlayerMap = new SmartMemoryPlayerManager(this.main, userFolder);
        this.userNameMap = new ConcurrentHashMap<>();

         this.main.getServer().getPluginManager().registerEvents(new PlayerJoin(this), this.main);
    }

    // DIVINITY ECONOMY CODE

    /**
     * Gets a player that does or doesn't exist.
     * This is the same as using query but query does not create users.
     * @param s - Player name
     * @return EconomyPlayer2
     */
    @Deprecated
    public EconomyPlayer get(String s) {
        // Attempt to find user in user name map (prevents having to run this loop every time)
        if (this.userNameMap.containsKey(s)) {
            return this.economyPlayerMap.get(this.userNameMap.get(s));
        }

        // Attempt to find player by name
        EconomyPlayer result = null;
        for (EconomyPlayer player : this.economyPlayerMap.values()) {
            if (player.getName().equalsIgnoreCase(s)) {
                result = player.update(null, s);
                break;
            }
        }

        // Else just create a new player.
        if (result == null) {
            result = this.economyPlayerMap.get(s);
        }

        // If the result is finally found, place name into map.
        if (result != null) {
            this.userNameMap.put(s, result.getFileID());
        }

        return result;
    }

    /**
     * Gets a player that does or doesn't exist.
     * This is the same as using query but query does not create users.
     * @param offlinePlayer
     * @return EconomyPlayer2
     */
    public EconomyPlayer get(OfflinePlayer offlinePlayer) {
        EconomyPlayer player = this.economyPlayerMap.get(offlinePlayer.getUniqueId());
        return player.update(offlinePlayer, null);
    }

    /**
     * A query simply searches for a player matching the given constraints
     * @param s - Player name
     * @return EconomyPlayer2
     */
    @Nullable
    public EconomyPlayer query(String s) {
        for (EconomyPlayer player : this.economyPlayerMap.values()) {
            if (player.getName().equalsIgnoreCase(s)) {
                return player;
            }
        }
        return null;
    }

    /**
     * A query simply searches for a player matching the given constraints
     * @param uuid
     * @return EconomyPlayer2
     */
    @Nullable
    public EconomyPlayer query(UUID uuid) {
        return this.economyPlayerMap.query(uuid);
    }

    /**
     * A query simply searches for a player matching the given constraints
     * @param offlinePlayer
     * @return EconomyPlayer2
     */
    @Nullable
    public EconomyPlayer query(OfflinePlayer offlinePlayer) {
        return this.query(offlinePlayer.getUniqueId());
    }

    /**
     * Returns an economy response detailing the result of the withdraw request
     * @param p - the player
     * @param v - value
     * @return EconomyResponse
     */
    public EconomyResponse withdrawPlayer(EconomyPlayer p, double v) {
        if (v < 0) {
            return new EconomyResponse(v, p.getBalance(), EconomyResponse.ResponseType.FAILURE, "negative amounts are not allowed");
        }

        if (!p.has(v)) {
            return new EconomyResponse(v, p.getBalance(), EconomyResponse.ResponseType.FAILURE, "withdrawal would lead to overdraft");
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
            return new EconomyResponse(v, p.getBalance(), EconomyResponse.ResponseType.FAILURE, "negative amounts are not allowed");

        if (!p.canHave(v))
            return new EconomyResponse(v, p.getBalance(), EconomyResponse.ResponseType.FAILURE, "balance may be too large");

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
        return false;
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
        return String.format("%,." + this.fractionalDigits + "f", v);
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
        return this.query(s) != null;
    }

    /**
     * Returns if the user given has an account
     * @param offlinePlayer
     * @return boolean
     */
    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return this.query(offlinePlayer) != null;
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
        return this.get(s).getBalance();
    }

    /**
     * Returns the balance of the player given
     * @param offlinePlayer
     * @return double
     */
    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return this.get(offlinePlayer).getBalance();
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

    /**
     * @param s
     * @param s1
     */
    @Override
    @Deprecated
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    /**
     * @param s
     * @param s1
     */
    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    /**
     * @param s
     * @param s1
     */
    @Override
    @Deprecated
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
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
}
