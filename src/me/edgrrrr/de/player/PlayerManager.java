package me.edgrrrr.de.player;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import me.edgrrrr.de.economy.NameStore;
import me.edgrrrr.de.utils.Converter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for managing players
 */
public class PlayerManager extends DivinityModule {
    private static final int MAX_SEARCH_DEPTH_INT = 64;
    private static final int MIN_SEARCH_DEPTH_INT = 4;
    private static final long MAX_SEARCH_NANO_LONG = 100000000L; // 100 millis
    private static final int PLAYER_TASK_INTERVAL = Converter.getTicks(60);
    private final Set<OfflinePlayer> players;
    private final Map<UUID, OfflinePlayer> playerUUIDs;
    private final PlayerLRUCache playerCache;
    private final Map<OfflinePlayer, NameStore> playerNames;
    private final BukkitRunnable playerTask = new BukkitRunnable() {
        @Override
        public void run() {
            fetchOfflinePlayers();
        }
    };

    /**
     * Constructor
     *
     * @param main - The main class
     */
    public PlayerManager(DEPlugin main) {
        super(main);
        this.players = Collections.synchronizedSet(new HashSet<>());
        this.playerNames = new ConcurrentHashMap<>();
        this.playerUUIDs = new ConcurrentHashMap<>();
        this.playerCache = new PlayerLRUCache(this.getMain());
    }


    /**
     * Initialisation of the object
     */
    @Override
    public void init() {
        this.playerTask.runTaskTimerAsynchronously(this.getMain(), PLAYER_TASK_INTERVAL, PLAYER_TASK_INTERVAL);
        fetchOfflinePlayers();
    }


    /**
     * Updates the internal offline player cache
     */
    protected void fetchOfflinePlayers() {
        this.getConsole().debug("Fetching players...");

        // Get offline players from server
        // Add players to store
        Set<OfflinePlayer> players = new HashSet<>(Arrays.asList(this.getMain().getServer().getOfflinePlayers()));

        // Check if cache should be invalidated
        if (players.size() != this.players.size()) {
            this.playerCache.clear();
            this.getConsole().debug("Players changed, invalidated player cache.");
        }

        // Update players
        synchronized (this.players) {
            this.players.clear();
            this.players.addAll(players);
        }

        // Update player names
        synchronized (this.playerNames) {
            this.playerNames.clear();
            for (OfflinePlayer player : players) {
                this.playerNames.put(player, new NameStore(player));
            }
        }

        // Update player UUIDs
        synchronized (this.playerUUIDs) {
            this.playerUUIDs.clear();
            for (OfflinePlayer player : players) {
                this.playerUUIDs.put(player.getUniqueId(), player);
            }
        }

        // Done
        this.getConsole().debug("Fetched %s players", this.players.size());
    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {
        this.playerTask.cancel();
    }


   /**
    * Returns all offline players
    * Note: this is a copy of the players set, so it is safe to modify
    * @return
    */
    public Set<OfflinePlayer> getPlayers() {
        return new HashSet<>(this.players);
    }


    /**
     * Returns the player name
     * @param player
     * @return
     */
    @Nonnull
    public NameStore getPlayerName(OfflinePlayer player) {
        synchronized (this.playerNames) {
            NameStore nameStore = this.playerNames.get(player);
            if (nameStore == null) {
                nameStore = new NameStore(player);
                this.playerNames.put(player, nameStore);
            }
            return nameStore;
        }
    }


    /**
     * Returns an offline player
     * Scans local offline players
     *
     * @param name       - name to scan for.
     * @return OfflinePlayer - the player corresponding to the name.
     */
    @Nullable
    public OfflinePlayer getPlayer(String name, boolean exact) {
        // If the call is exact, return the player
        if (exact) {
            return this.getMain().getServer().getOfflinePlayer(name);
        }

        // Get players
        Set<OfflinePlayer> players = this.getPlayers(name);

        // Return the first player
        for (OfflinePlayer player : players) {
            return player;
        }

        // No player found
        return null;
    }


    public OfflinePlayer getPlayer(UUID uuid) {
        // Get player from cache
        OfflinePlayer player = this.playerUUIDs.getOrDefault(uuid, null);

        // If player is not cached, search for them
        if (player == null) {
            player = this.getMain().getServer().getOfflinePlayer(uuid);
        }

        // Update cache
        synchronized (this.playerUUIDs) {
            this.playerUUIDs.put(uuid, player);
        }

        // Return player
        return player;
    }


    /**
     * Gets all offline players whose name matched the given term
     *
     * @param term
     */
    public Set<OfflinePlayer> getPlayers(String term) {
        // Get players from cache
        Set<OfflinePlayer> players = this.playerCache.get(term);

        // If players are not cached, search for them
        if (players == null) {
            players = this.searchPlayers(term);
            this.playerCache.put(term, players);
        }

        return players;
    }


    /**
     * Gets all offline players whose name matched the given term
     * @param term
     * @return
     */
    protected Set<OfflinePlayer> searchPlayers(String term) {
        term = term.toLowerCase().strip(); // Standardise term
        HashSet<OfflinePlayer> players = new HashSet<>(); // Create itemNames array

        // Priority store
        HashSet<OfflinePlayer> priority0ArrayList = new HashSet<>();
        HashSet<OfflinePlayer> priority1ArrayList = new HashSet<>();
        HashSet<OfflinePlayer> priority2ArrayList = new HashSet<>();
        HashSet<OfflinePlayer> priority3ArrayList = new HashSet<>();

        // Start time
        long startTime = System.nanoTime();

        // Loop through items, add any item that
        // - contains <term>
        // - equals <term>
        // - startswith <term>
        // - endswith <term>
        synchronized (this.players) {
            for (OfflinePlayer offlinePlayer : this.players) {
                // Check max search depth
                int depth = priority0ArrayList.size() + priority1ArrayList.size() + priority2ArrayList.size() + priority3ArrayList.size();
                if (depth > MAX_SEARCH_DEPTH_INT) {
                    this.getConsole().debug("Max search depth reached, stopping search.");
                    break;
                }

                // Check max search time
                if (System.nanoTime() - startTime > MAX_SEARCH_NANO_LONG && depth >= MIN_SEARCH_DEPTH_INT) {
                    this.getConsole().debug("Max search time reached, stopping search.");
                    break;
                }

                // Get player name
                NameStore nameStore = this.getPlayerName(offlinePlayer);
                if (nameStore == null) continue;
                String playerName = nameStore.name().toLowerCase().strip();

                // Matches - priority 0
                if (playerName.equalsIgnoreCase(term)) {
                    priority0ArrayList.add(offlinePlayer);
                    continue;
                }

                // Begins with - priority 1
                if (playerName.startsWith(term)) {
                    priority1ArrayList.add(offlinePlayer);
                    continue;
                }

                // Contains - priority 2
                if (playerName.contains(term)) {
                    priority2ArrayList.add(offlinePlayer);
                    continue;
                }

                // Endswith - priority 3
                if (playerName.endsWith(term)) {
                    priority3ArrayList.add(offlinePlayer);
                    continue;
                }
            }
        }

        // Add by priority
        players.addAll(priority0ArrayList);
        players.addAll(priority1ArrayList);
        players.addAll(priority2ArrayList);
        players.addAll(priority3ArrayList);

        // Done
        this.getConsole().debug("Found %s players matching '%s' in %s milliseconds.", players.size(), term, (System.nanoTime() - startTime) / 1000000);

        // Return
        return players;
    }


    /**
     * Gets all names of offline players whose name matched the given term
     */
    public String[] getPlayerNames(String term) {
        Set<OfflinePlayer> players = this.getPlayers(term);
        Set<String> playerNames = new HashSet<>();
        for (OfflinePlayer player : players) {
            playerNames.add(player.getName());
        }
        return playerNames.toArray(new String[0]);
    }


    /**
     * Adds the itemstacks in itemStacks to player
     * Note that this does not calculate if all of the materials will fit in the players inventory
     *
     * @param player     - The player to add the materials to
     * @param itemStacks - The itemStacks to add
     */
    public static void addPlayerItems(Player player, ItemStack[] itemStacks) {
        Inventory inventory = player.getInventory();
        for (ItemStack itemStack : itemStacks) {
            inventory.addItem(itemStack);
        }
    }

    /**
     * Adds the itemstack to the player
     * Note that this does not calculate if all of the materials will fit in the players inventory
     *
     * @param player    - The player to add the items to
     * @param itemStack - The itemstacks to add
     */
    public static void addPlayerItems(Player player, ItemStack itemStack) {
        player.getInventory().addItem(itemStack);
    }

    /**
     * Returns the number of empty slots in a players inventory.
     *
     * @param player - The player to check
     * @return int - The number of empty slots
     */
    public static int getEmptySlots(Player player) {
        int count = 0;
        ItemStack[] inventory = player.getInventory().getStorageContents();

        for (ItemStack iStack : inventory) {
            if (iStack == null) {
                count += 1;
            }
        }

        return count;
    }

    /**
     * Replaces the itemstack1 with the itemstack2
     *
     * @param player
     * @param itemStack1
     * @param itemStack2
     */
    public static void replaceItemStack(Player player, ItemStack itemStack1, ItemStack itemStack2) {
        removePlayerItems(itemStack1);
        addPlayerItems(player, itemStack2);
    }

    /**
     * Replaces the itemstacks1 with the itemstacks2
     *
     * @param player
     * @param itemStacks1
     * @param itemStacks2
     */
    public static void replaceItemStacks(Player player, ItemStack[] itemStacks1, ItemStack[] itemStacks2) {
        removePlayerItems(itemStacks1);
        addPlayerItems(player, itemStacks2);
    }

    /**
     * Loops through the items and removes them from the players inventory
     */
    public static void removePlayerItems(ItemStack[] itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            removePlayerItems(itemStack);
        }
    }

    /**
     * Removes the given item stack from the given player
     *
     * @param itemStack
     */
    public static void removePlayerItems(ItemStack itemStack) {
        itemStack.setAmount(0);
    }

    /**
     * Returns the item the user is holding
     */
    public static ItemStack getHeldItem(Player player) {
        int slotIdx = player.getInventory().getHeldItemSlot();
        return player.getInventory().getItem(slotIdx);
    }

    /**
     * Returns the item the user is holding, but returns the fallback if null.
     */
    public static ItemStack getHeldItem(Player player, ItemStack fallback) {
        ItemStack heldItem = getHeldItem(player);
        if (heldItem == null) heldItem = fallback;
        return heldItem;
    }

    /**
     * Returns a string of the names of materials in the players inventory.
     */
    public static Set<String> getInventoryMaterialNames(Player player) {
        ItemStack[] materials = getInventoryMaterials(player);
        Set<String> uniqueMaterialIDs = new HashSet<>();
        for (ItemStack material : materials) uniqueMaterialIDs.add(material.getType().name());
        return uniqueMaterialIDs;
    }

    public static ItemStack[] getInventoryMaterials(Player player) {
        ItemStack[] materials = player.getInventory().getStorageContents();
        ArrayList<ItemStack> materialList = new ArrayList<>();
        for (ItemStack iStack : materials) {
            if (iStack != null) {
                materialList.add(iStack);
            }
        }

        return materialList.toArray(new ItemStack[0]);
    }

}
