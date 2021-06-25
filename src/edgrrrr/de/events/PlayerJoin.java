package edgrrrr.de.events;

import edgrrrr.de.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final Economy economy;

    public PlayerJoin(Economy economy) {
        this.economy = economy;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.economy.hasAccount(player)) {
            this.economy.createPlayerAccount(player);
            //TODO: send message
        }
    }
}
