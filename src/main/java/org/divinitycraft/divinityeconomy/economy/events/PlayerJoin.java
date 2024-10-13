package org.divinitycraft.divinityeconomy.economy.events;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.console.Console;
import org.divinitycraft.divinityeconomy.economy.DivinityEconomy;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final DEPlugin main;
    private final DivinityEconomy divinityEconomy;

    public PlayerJoin(DEPlugin main, DivinityEconomy divinityEconomy) {
        this.main = main;
        this.divinityEconomy = divinityEconomy;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.divinityEconomy.hasAccount(player)) {
            this.divinityEconomy.createPlayerAccount(player);
            Console.get().debug(LangEntry.ECONOMY_CreatingPlayerFile.get(this.main), player.getName(), player.getUniqueId());
        }
    }
}
