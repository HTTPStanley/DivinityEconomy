package org.divinitycraft.divinityeconomy.economy;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.DivinityModule;
import org.divinitycraft.divinityeconomy.config.Setting;
import org.divinitycraft.divinityeconomy.economy.players.EconomyPlayer;
import org.divinitycraft.divinityeconomy.response.EconomyTransferResponse;
import org.divinitycraft.divinityeconomy.utils.Converter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An economy manager to simplify tasks for managing the player economy, works with Vault Economy.
 */
public class EconomyManager extends DivinityModule {

    // ListBalances task
    private static final int BALTOP_PAGE_SIZE = 10;
    private Map<Integer, BaltopPlayer[]> orderedBalances = new ConcurrentHashMap<>();
    private Map<OfflinePlayer, Integer> baltopPositionCache = new ConcurrentHashMap<>();
    private final Calendar lastOrderTime = Calendar.getInstance();
    private double totalEconomySize = 0;
    private int totalEconomyPlayers = 0;

    private final BukkitRunnable baltopTask = new BukkitRunnable() {
        @Override
        public void run() {
            {
                try {
                    fetchBaltop();
                } catch (Exception e) {
                    getConsole().warn("Failed to fetch baltop (This is likely an issue with your economy provider): %s", e.getMessage());
                }
            }
        }
    };

    // Settings
    public double minTransfer;
    public double minBalance;

    // Stores the Vault economy api
    private DivinityEconomy divinityEconomy;
    private Economy economy;

    public EconomyManager(DEPlugin main) {
        super(main, false);
    }

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        // settings
        this.minTransfer = this.getConfMan().getDouble(Setting.ECONOMY_MIN_SEND_AMOUNT_DOUBLE);
        this.minBalance = 0d;
        this.divinityEconomy = new DivinityEconomy(getMain());
        this.economy = setupEconomy(divinityEconomy);

        // Register economy service
        divinityEconomy.startTasks();

        // Setup baltop task scheduler
        int timer = Converter.getTicks(Converter.constrainInt(getMain().getConfMan().getInt(Setting.ECONOMY_BALTOP_REFRESH_INTEGER), 60, 3600));
        this.baltopTask.runTaskTimerAsynchronously(getMain(), timer, timer);
        try {
            fetchBaltop();
        } catch (Exception e) {
            getConsole().warn("Failed to fetch baltop on startup (This is likely an issue with your economy provider): %s", e.getMessage());
        }
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {
        this.baltopTask.cancel();

        // Register economy service
        divinityEconomy.stopTasks();
    }

    /**
     * Fetches the baltop
     */
    public void fetchBaltop() {
        this.getConsole().debug("Fetching baltop...");
        // Total economy size
        double totalSize = 0;

        // Get players
        ArrayList<BaltopPlayer> players = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : getMain().getPlayMan().getPlayers()) {
            // Get balance
            double balance = getBalance(offlinePlayer);

            // Add player
            players.add(new BaltopPlayer(offlinePlayer, balance, getMain().getPlayMan().getPlayerName(offlinePlayer)));

            // Add to total size
            totalSize += balance;
        }

        // Sort players
        players.sort((a, b) -> Double.compare(b.getBalance(), a.getBalance()));

        // Sort into pages
        List<List<BaltopPlayer>> pages = new ArrayList<>();
        pages.add(new ArrayList<>());

        // Paginate players
        Map<OfflinePlayer, Integer> positionCache = new ConcurrentHashMap<>();
        int position = 1;
        int index = 0;
        for (BaltopPlayer player : players) {
            pages.get(pages.size() - 1).add(player);

            // Add to position cache
            positionCache.put(player.getOfflinePlayer(), position);

            if (index == BALTOP_PAGE_SIZE) {
                pages.add(new ArrayList<>());
                index = 0;
            } else {
                index++;
            }

            position++;
        }

        // Convert to map
        Map<Integer, BaltopPlayer[]> playersByPage = new ConcurrentHashMap<>();
        for (int i = 0; i < pages.size(); i++) {
            playersByPage.put(i, pages.get(i).toArray(new BaltopPlayer[0]));
        }

        // Set values
        this.orderedBalances.clear();
        this.orderedBalances = playersByPage;
        this.baltopPositionCache.clear();
        this.baltopPositionCache = positionCache;
        this.totalEconomySize = totalSize;
        this.totalEconomyPlayers = players.size();
        this.lastOrderTime.setTimeInMillis(System.nanoTime());
        this.getConsole().debug("Baltop fetched %s players.", players.size());
    }


    /**
     * Returns the economy player object
     * @param player
     * @return
     */
    public EconomyPlayer getPlayer(OfflinePlayer player) {
        return this.divinityEconomy.get(player);
    }


    /**
     * Returns the baltop position cache
     * @return
     */
    public Map<OfflinePlayer, Integer> getBaltopPositionCache() {
        return this.baltopPositionCache;
    }


    /**
     * Returns the baltop position of a player
     * @param player
     * @return
     */
    public int getBaltopPosition(OfflinePlayer player) {
        return this.baltopPositionCache.getOrDefault(player, 0);
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
     * Returns the total number of players in the economy
     * @return
     */
    public int getTotalEconomyPlayers() {
        return this.totalEconomyPlayers;
    }

    /**
     * Returns the average economy size per player
     * @return
     */
    public double getEconomySizePerCapita() {
        if (this.totalEconomyPlayers == 0) {
            return 0;
        }
        return this.totalEconomySize / this.totalEconomyPlayers;
    }


    /**
     * Returns the economy equality score
     * @return
     */
    public int getEconomyEquality() {
        if (this.totalEconomyPlayers == 0) {
            return 0; // No players, no economy equality.
        }

        double average = getEconomySizePerCapita(); // Get the average balance.
        double meanDeviation = 0;
        double allowedDeviation = average * 0.10; // 10% of the average balance.

        for (OfflinePlayer player : getMain().getPlayMan().getPlayers()) {
            double balance = getBalance(player);
            double deviation = Math.abs(balance - average); // Deviation from average.

            // If the deviation is larger than allowed, count it fully, otherwise it's reduced.
            if (deviation > allowedDeviation) {
                meanDeviation += deviation;
            } else {
                meanDeviation += deviation * 0.5; // Apply reduced impact for smaller deviations.
            }
        }

        // Normalize the deviation into a 0-100 scale where 0 is worst equality and 100 is perfect equality.
        double maxDeviation = average * this.totalEconomyPlayers; // Hypothetical maximum deviation.
        double score = 100 - (meanDeviation / maxDeviation) * 100; // Convert deviation to score out of 100.

        // Ensure the score is between 0 and 100.
        return Math.max(0, Math.min(100, (int) score));
    }


    /**
     * Returns the richest player
     * @return
     */
    public BaltopPlayer getRichestPlayer() {
        if (this.orderedBalances.isEmpty()) {
            return null;
        }
        return this.orderedBalances.get(0)[0];
    }


    /**
     * Returns the top percentile of players
     * @return
     */
    public BaltopPlayer[] getTopPercentile(int percentile) {
        if (this.orderedBalances.isEmpty()) {
            return new BaltopPlayer[0];
        }

        int topPercentile = (int) (this.totalEconomyPlayers * (percentile / 100.0));
        ArrayList<BaltopPlayer> topPlayers = new ArrayList<>();
        int index = 0;
        for (BaltopPlayer[] players : this.orderedBalances.values()) {
            for (BaltopPlayer player : players) {
                topPlayers.add(player);
                index++;
                if (index >= topPercentile) {
                    break;
                }
            }
            if (index >= topPercentile) {
                break;
            }
        }

        return topPlayers.toArray(new BaltopPlayer[0]);
    }


    /**
     * Returns the bottom percentile of players
     * @return
     */
    public BaltopPlayer[] getBottomPercentile(int percentile) {
        if (this.orderedBalances.isEmpty()) {
            return new BaltopPlayer[0];
        }

        int bottomPercentile = (int) (this.totalEconomyPlayers * (percentile / 100.0));
        ArrayList<BaltopPlayer> bottomPlayers = new ArrayList<>();
        int index = 0;
        for (BaltopPlayer[] players : this.orderedBalances.values()) {
            for (BaltopPlayer player : players) {
                bottomPlayers.add(player);
                index++;
                if (index >= bottomPercentile) {
                    break;
                }
            }
            if (index >= bottomPercentile) {
                break;
            }
        }

        return bottomPlayers.toArray(new BaltopPlayer[0]);
    }


    /**
     * Returns the total balance of the bottom percentile
     * @return
     */
    public double getTopPercentileBalance(int percentile) {
        double total = 0;
        for (BaltopPlayer player : getTopPercentile(percentile)) {
            total += player.getBalance();
        }
        return total;
    }


    /**
     * Returns the total balance of the bottom percentile
     * @return
     */
    public double getBottomPercentileBalance(int percentile) {
        double total = 0;
        for (BaltopPlayer player : getBottomPercentile(percentile)) {
            total += player.getBalance();
        }
        return total;
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
    public Map<Integer, BaltopPlayer[]> getOrderedBalances() {
        return this.orderedBalances;
    }


    /**
     * Registers the economy provider
     *
     * @param economy - The economy provider
     */
    public void registerProvider(Economy economy) {
        getMain().getServer().getServicesManager().register(Economy.class, economy, getMain(), ServicePriority.Normal);
    }


    /**
     * Returns the economy provider
     *
     * @return Economy
     */
    @Nullable
    public Economy getProvider() {
        return getMain().getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }


    /**
     * Sets up the vault economy object
     * Returns if it was successful or not.
     */
    @Nullable
    public Economy setupEconomy(DivinityEconomy divinityEconomy) {
        // Look for vault
        if (getMain().getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getConsole().warn("No plugin 'Vault' detected, you must have Vault to use this plugin...");
            return null;
        }
        this.getConsole().info("Vault detected.");

        // Set up economy
        this.registerProvider(divinityEconomy);

        // return if economy was gotten successfully.
        return this.getProvider();
    }

    /**
     * Returns the vault economy api
     *
     * @return Economy
     */
    public Economy getVaultEconomy() {
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
        this.getConsole().debug("ADD REQUEST FOR %s %s", oPlayer.getName(), getMain().getConsole().formatMoney(amount));
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
        this.getConsole().debug("REM REQUEST FOR %s %s", oPlayer.getName(), getMain().getConsole().formatMoney(amount));
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
        this.getConsole().debug("SET REQUEST FOR %s %s", oPlayer.getName(), getMain().getConsole().formatMoney(amount));
        double balance = this.getBalance(oPlayer);
        double difference = amount - balance;
        EconomyResponse response;
        if (difference < 0) {
            response = this.remCash(oPlayer, -difference);
        } else if (difference > 0) {
            response = this.addCash(oPlayer, difference);
        } else {
            response = new EconomyResponse(difference, this.getBalance(oPlayer), EconomyResponse.ResponseType.SUCCESS, "");
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
            response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, EconomyResponse.ResponseType.FAILURE, "cannot send money to yourself!");
        } else {
            // Ensure amount is above or equal to the minimum send amount
            if (amount < this.minTransfer) {
                response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, EconomyResponse.ResponseType.FAILURE, String.format("cannot send less than %s", getMain().getConsole().formatMoney(this.minTransfer)));
            } else {

                // Take money from sender
                EconomyResponse takeResponse = this.remCash(from, amount);
                if (takeResponse.type == EconomyResponse.ResponseType.FAILURE) {
                    response = new EconomyTransferResponse(fromBalance, toBalance, 0.0, EconomyResponse.ResponseType.FAILURE, takeResponse.errorMessage);

                } else {

                    // Send money to receiver
                    EconomyResponse sendResponse = this.addCash(to, amount);
                    if (sendResponse.type == EconomyResponse.ResponseType.FAILURE) {
                        // Since takeResponse was a success
                        // We have to reset their balance
                        this.setCash(from, fromBalance);
                        response = new EconomyTransferResponse(this.getBalance(from), toBalance, 0.0, EconomyResponse.ResponseType.FAILURE, sendResponse.errorMessage);
                    } else {
                        response = new EconomyTransferResponse(this.getBalance(from), this.getBalance(from), amount, EconomyResponse.ResponseType.SUCCESS, "");
                    }
                }
            }
        }
        return response;
    }
}
