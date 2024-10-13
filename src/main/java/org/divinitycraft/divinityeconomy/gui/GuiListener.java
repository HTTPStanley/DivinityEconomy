package org.divinitycraft.divinityeconomy.gui;

import org.divinitycraft.divinityeconomy.Constants;
import org.divinitycraft.divinityeconomy.DEPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;

public class GuiListener implements Listener {
    private final DEPlugin main;
    private final Inventory inv;

    public GuiListener(DEPlugin main) {
        this.main = main;
        this.inv = Bukkit.createInventory(null, Constants.INVENTORY_SIZE, "Divinity Economy");

        // Register the listener
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Storage GUI")) {
            event.setCancelled(true);  // Prevent player from taking items

            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();

            // Perform different actions based on which button is clicked
            if (slot == 11) {
                player.performCommand("examplecommand1");
                player.sendMessage("You clicked Button 1!");
            } else if (slot == 15) {
                player.performCommand("examplecommand2");
                player.sendMessage("You clicked Button 2!");
            }
        }
    }

    public void openStorageGUI(@Nonnull Player player) {
        player.openInventory(inv);
    }

    public void closeStorageGUI(@Nonnull Player player) {
        player.closeInventory();
    }

    public DEPlugin getMain() {
        return main;
    }
}
