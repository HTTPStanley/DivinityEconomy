package me.edgrrrr.de.world.events;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.world.WorldManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class WorldNotification implements Listener {
    private final DEPlugin main;
    private final WorldManager worldManager;


    public WorldNotification(DEPlugin main, WorldManager worldManager) {
        this.main = main;
        this.worldManager = worldManager;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        this.handleEvent(player, world);
    }


    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World toWorld = player.getWorld();
        this.handleEvent(player, toWorld);
    }


    public void handleEvent(Player player, World world) {
        try {
            // Check if they have notifications enabled
            if (!this.main.getEconMan().getPlayer(player).getNotification()) {
                return;
            }

            // Check if the world has the market and economy enabled
            boolean marketEnabled = this.worldManager.isMarketEnabled(world);
            boolean economyEnabled = this.worldManager.isEconomyEnabled(world);

            // Conditional message
            if (marketEnabled && economyEnabled) {
                this.main.getConsole().info(player, LangEntry.WORLDS_BothEnabled.get(main));
            } else if (marketEnabled) {
                this.main.getConsole().info(player, LangEntry.WORLDS_MarketEnabled.get(main));
            } else if (economyEnabled) {
                this.main.getConsole().info(player, LangEntry.WORLDS_EconomyEnabled.get(main));
            } else {
                this.main.getConsole().info(player, LangEntry.WORLDS_BothDisabled.get(main));
            }
        } catch (Exception ignored) {
        }
    }
}