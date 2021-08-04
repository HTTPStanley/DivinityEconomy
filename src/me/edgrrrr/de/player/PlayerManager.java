package me.edgrrrr.de.player;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.DivinityModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A class for managing players
 */
public class PlayerManager extends DivinityModule {

    /**
     * Constructor
     *
     * @param main - The main class
     */
    public PlayerManager(DEPlugin main) {
        super(main);
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
    public static String[] getInventoryMaterialNames(Player player) {
        ItemStack[] materials = getInventoryMaterials(player);
        Set<String> uniqueMaterialIDs = new HashSet<>();
        for (ItemStack material : materials) uniqueMaterialIDs.add(material.getType().name());
        return uniqueMaterialIDs.toArray(new String[0]);
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

    /**
     * Initialisation of the object
     */
    @Override
    public void init() {

    }

    /**
     * Shutdown of the object
     */
    @Override
    public void deinit() {

    }

    /**
     * If the player is online or offline
     * Equal to player.getPlayer() == null
     *
     * @param player - The player to check
     * @return boolean
     */
    public boolean playerIsOnline(OfflinePlayer player) {
        return (player.getPlayer() == null);
    }

    /**
     * The player, if they are online
     *
     * @param player - The player
     * @return Player
     */
    public Player getPlayer(OfflinePlayer player) {
        return player.getPlayer();
    }

    /**
     * Returns an offline player
     * Scans local offline players
     * If allow fetch is enabled, then will find fetch player from the web.
     *
     * @param name       - name to scan for.
     * @param allowFetch - Uses deprecated "bukkit.getOfflinePlayer".
     * @return OfflinePlayer - the player corresponding to the name.
     */
    public OfflinePlayer getOfflinePlayer(String name, boolean allowFetch) {
        OfflinePlayer player = null;
        OfflinePlayer[] oPlayers = this.getMain().getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : oPlayers) {
            String oPlayerName = oPlayer.getName();
            if (oPlayerName != null) {
                if (oPlayerName.toLowerCase().trim().equals(name.trim().toLowerCase())) {
                    player = oPlayer;
                    break;
                }
            }
        }

        if (allowFetch && (player == null)) {
            player = this.getMain().getServer().getOfflinePlayer(name);
        }

        return player;
    }

    /**
     * Gets an offline player by their UUID
     * Scans only local players unless allowFetch is enabled, which will allow it to scan the web
     *
     * @param uuid       - The uuid the find
     * @param allowFetch - Whether to scan the web or not
     * @return OfflinePlayer - can be null.
     */
    public OfflinePlayer getOfflinePlayer(UUID uuid, boolean allowFetch) {
        OfflinePlayer player = null;
        OfflinePlayer[] offlinePlayers = this.getMain().getServer().getOfflinePlayers();
        for (OfflinePlayer oPlayer : offlinePlayers) {
            if (oPlayer.getUniqueId().equals(uuid)) {
                player = oPlayer;
                break;
            }
        }

        if (allowFetch && (player == null)) {
            player = this.getMain().getServer().getOfflinePlayer(uuid);
        }

        return player;
    }

    /**
     * Gets all offline players who's name starts with startswith
     *
     * @param startsWith
     */
    public OfflinePlayer[] getOfflinePlayers(String startsWith) {
        OfflinePlayer[] offlinePlayers = this.getMain().getServer().getOfflinePlayers();
        ArrayList<OfflinePlayer> players = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName() == null) continue;
            if (offlinePlayer.getName().toLowerCase().startsWith(startsWith.toLowerCase(Locale.ROOT))) {
                players.add(offlinePlayer);
            }
        }

        return players.toArray(new OfflinePlayer[0]);
    }

    /**
     * Gets all names of offline players
     */
    public String[] getOfflinePlayerNames() {
        OfflinePlayer[] offlinePlayers = this.getMain().getServer().getOfflinePlayers();
        ArrayList<String> playerNames = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            String name = offlinePlayer.getName();
            if (name == null) continue;

            playerNames.add(name);
        }

        return playerNames.toArray(new String[0]);
    }

    /**
     * Gets all names of offline players who's name starts with startswith
     */
    public String[] getOfflinePlayerNames(String startsWith) {
        OfflinePlayer[] offlinePlayers = this.getOfflinePlayers(startsWith);
        ArrayList<String> playerNames = new ArrayList<>();
        for (OfflinePlayer player : offlinePlayers) {
            playerNames.add(player.getName());
        }

        return playerNames.toArray(new String[0]);
    }
}
