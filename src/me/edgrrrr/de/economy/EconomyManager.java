package me.edgrrrr.de.economy;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.config.Setting;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.response.EconomyTransferResponse;
import me.edgrrrr.de.utils.ArrayUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An economy manager to simplify tasks for managing the player economy, works with Vault Economy.
 */
public class EconomyManager extends DivinityModule {

    // Baltop task
    private Map<Integer, Map.Entry<UUID, Double>[]> orderedBalances = new ConcurrentHashMap<>();
    private final Calendar lastOrderTime = Calendar.getInstance();
    private double totalEconomySize = 0;

    private final BukkitRunnable baltopTask = new BukkitRunnable() {
        @Override
        public void run() {
            {
                // Get all players, sort & place into pages
                Map<UUID, Double> players = new ConcurrentHashMap<>();
                for (String playerName : getMain().getPlayMan().getOfflinePlayerNames()) {
                    OfflinePlayer player = getMain().getPlayMan().getOfflinePlayer(playerName, true);
                    players.put(player.getUniqueId(), getBalance(player));
                }
                ArrayList<Map.Entry<UUID, Double>> sortedPlayers = new ArrayList<>(players.entrySet());
                Collections.sort(sortedPlayers, Map.Entry.comparingByValue());
                Collections.reverse(sortedPlayers);
                Map<Integer, Map.Entry<UUID, Double>[]> playersByPage = new ConcurrentHashMap<>();
                for (Map.Entry<Integer, List<Object>> entry : ArrayUtils.toPages(sortedPlayers.toArray(new Map.Entry[0]), 10).entrySet()) {
                    ArrayList<Map.Entry<UUID, Double>> entries = new ArrayList<>();
                    entry.getValue().forEach(obj -> entries.add((Map.Entry<UUID, Double>) obj));
                    playersByPage.put(entry.getKey(), entries.toArray(new Map.Entry[0]));
                }

                // Sum all player balances
                double tempTotalSize = 0;
                for (Map.Entry<UUID, Double>[] entries : playersByPage.values()) {
                    for (Map.Entry<UUID, Double> entry : entries) {
                        tempTotalSize += entry.getValue();
                    }
                }

                orderedBalances.clear();
                orderedBalances = playersByPage;
                totalEconomySize = tempTotalSize;
                lastOrderTime.setTimeInMillis(System.nanoTime());
            }
        }
    };

    // Settings
    public double minTransfer;
    public double minBalance;
    private String providerName;

    // Stores the Vault economy api
    private net.milkbowl.vault.economy.Economy economy;

    public EconomyManager(DEPlugin main) {
        super(main);
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        // settings
        this.minTransfer = this.getConfMan().getDouble(Setting.ECONOMY_MIN_SEND_AMOUNT_DOUBLE);
        this.minBalance = this.getConfMan().getDouble(Setting.ECONOMY_MIN_BALANCE_DOUBLE);
        this.providerName = this.getConfMan().getString(Setting.ECONOMY_PROVIDER_STRING);

        // If economy setup fails, plugin shuts down
        if (!this.setupEconomy()) {
            this.getMain().shutdown();
        }

        // Setup baltop task scheduler
        int timer = Math.getTicks(this.getMain().getConfMan().getInt(Setting.ECONOMY_BALTOP_REFRESH_INTEGER));
        this.baltopTask.runTaskTimerAsynchronously(this.getMain(), 0, timer);
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {
        this.baltopTask.cancel();
    }

    /**
     * Returns the last time the baltop was ordered
     * @return
     */
    public Calendar getLastOrderTime() {
        return this.lastOrderTime;
    }

    /**
     * Returns the total sum of cash in the economy
     * @return
     */
    public double getTotalEconomySize() {
        return this.totalEconomySize;
    }

    /**
     * Returns a map containing pages of 10 players.
     * 15 players would return:
     * 0 -> Entry<UUID, Double>[10]
     * 1 -> Entry<UUID, Double>[5]
     * Key: UUID is player UUID
     * Value: Double is balance
     * @return
     */
    public Map<Integer, Map.Entry<UUID, Double>[]> getOrderedBalances() {
        return this.orderedBalances;
    }

    public Collection<RegisteredServiceProvider<net.milkbowl.vault.economy.Economy>> getProviders() {
        return this.getMain().getServer().getServicesManager().getRegistrations(net.milkbowl.vault.economy.Economy.class);
    }

    public RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> getPrimaryProvider() {
        for (RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> provider : this.getProviders()) {
            if (provider.getPlugin().getName().equals(this.providerName)) {
                return provider;
            }
        }
        return this.getMain().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
    }

    public Collection<RegisteredServiceProvider<net.milkbowl.vault.economy.Economy>> setProvider(net.milkbowl.vault.economy.Economy economy) {
        this.getMain().getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, economy, this.getMain(), ServicePriority.Normal);
        return this.getProviders();
    }


    /**
     * Sets up the vault economy object
     * Returns if it was successful or not.
     */
    public boolean setupEconomy() {
        DivinityEconomy divinityEconomy = new DivinityEconomy(this.getMain(), this.getConfMan(), this.getConsole(), this.getPlayMan(), this.getConfMan().getInt(Setting.CHAT_ECONOMY_DIGITS_INT), this.getConfMan().getString(Setting.CHAT_ECONOMY_PLURAL_STRING), this.getConfMan().getString(Setting.CHAT_ECONOMY_SINGULAR_STRING));

        // Look for vault
        if (this.getMain().getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getConsole().warn("No plugin 'Vault' detected, this will likely cause issues with plugins not cooperating.");
            this.economy = divinityEconomy;

        } else {
            this.getConsole().info("Vault has been detected.");


            // Get the service provider
            Collection<RegisteredServiceProvider<net.milkbowl.vault.economy.Economy>> providers = this.setProvider(divinityEconomy);
            if (providers.size() == 0) {
                this.getConsole().severe("Could not register Economy.");
                return false;
            } else {
                this.economy = this.getPrimaryProvider().getProvider();
            }

            for (RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> provider : providers) {
                this.getConsole().info("Registered Economy Provider: '%s' (primary = %s) (selected = %s)", provider.getPlugin().getName(), provider.equals(this.getPrimaryProvider()), provider.getPlugin().getName().equals(this.providerName));
            }
            this.getConsole().info("Total Economy Providers: %d", providers.size());
        }

        // return if economy was gotten successfully.
        return true;
    }

    /**
     * Returns the vault economy api
     *
     * @return Economy
     */
    public net.milkbowl.vault.economy.Economy getVaultEconomy() {
        return this.economy;
    }

    /**
     * Gets the players balance
     *
     * @param player - The player to get the balance for
     * @return double
     */
    public double getBalance(OfflinePlayer player) {
        return this.economy.getBalance(player);
    }

    /**
     * Adds <amount> to <player>
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     */
    public EconomyResponse addCash(OfflinePlayer oPlayer, double amount) {
        this.getConsole().debug("ADD REQUEST FOR %s £%,.2f", oPlayer.getName(), amount);
        EconomyResponse response = this.economy.depositPlayer(oPlayer, amount);
        this.getConsole().debug("ADD RESULT: %s %s", response.transactionSuccess(), response.errorMessage);
        return response;
    }

    /**
     * Removes <amount> from <player>
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     */
    public EconomyResponse remCash(OfflinePlayer oPlayer, double amount) {
        this.getConsole().debug("REM REQUEST FOR %s £%,.2f", oPlayer.getName(), amount);
        EconomyResponse response = this.economy.withdrawPlayer(oPlayer, amount);
        this.getConsole().debug("REM RESULT: %s %s", response.transactionSuccess(), response.errorMessage);

        return response;
    }

    /**
     * Sets the balance of a player to the amount provided
     *
     * @param oPlayer - The offline player
     * @param amount  - The amount
     * @return EconomyResponse - The result of the function
     */
    public EconomyResponse setCash(OfflinePlayer oPlayer, double amount) {
        this.getConsole().debug("SET REQUEST FOR %s £%,.2f", oPlayer.getName(), amount);
        double balance = this.getBalance(oPlayer);
        double difference = amount - balance;
        EconomyResponse response;
        if (difference < 0) {
            response = this.remCash(oPlayer, -difference);
        } else if (difference > 0) {
            response = this.addCash(oPlayer, difference);
        } else {
            response = new EconomyResponse(difference, this.getBalance(oPlayer), ResponseType.SUCCESS, "");
        }

        this.getConsole().debug("SET RESULT: %s %s", response.transactionSuccess(), response.errorMessage);

        return response;
    }

    /**
     * Removes amount <amount> from <from>
     * Adds amount <amount> to <to>
     * Can fail if criteria aren't met:
     * -minimumSendAmount <
     * -<from> has <amount> to send
     *
     * @param from   - The source player of cash
     * @param to     - The result player of cash
     * @param amount - The amount of cash
     * @return EconomyResponse - The result of the function
     */
    public EconomyTransferResponse sendCash(OfflinePlayer from, OfflinePlayer to, double amount) {
        // The response
        EconomyTransferResponse response;
        double fromBalance = this.getBalance(from);
        double toBalance = this.getBalance(to);

        if (from == to) {
            response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, ResponseType.FAILURE, "cannot send money to yourself!");
        } else {
            // Ensure amount is above or equal to the minimum send amount
            if (amount < this.minTransfer) {
                response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, ResponseType.FAILURE, String.format("cannot send less than £%f", this.minTransfer));
            } else {

                // Take money from sender
                EconomyResponse takeResponse = this.remCash(from, amount);
                if (takeResponse.type == ResponseType.FAILURE) {
                    response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, ResponseType.FAILURE, takeResponse.errorMessage);

                } else {

                    // Send money to receiver
                    EconomyResponse sendResponse = this.addCash(to, amount);
                    if (sendResponse.type == ResponseType.FAILURE) {
                        // Since takeResponse was a success
                        // We have to reset their balance
                        this.setCash(from, fromBalance);
                        response = new EconomyTransferResponse(this.getBalance(from), toBalance, 0.0, ResponseType.FAILURE, sendResponse.errorMessage);
                    } else {
                        response = new EconomyTransferResponse(this.getBalance(from), this.getBalance(from), amount, ResponseType.SUCCESS, "");
                    }
                }
            }
        }
        return response;
    }
}
