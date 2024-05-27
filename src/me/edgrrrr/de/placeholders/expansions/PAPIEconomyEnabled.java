package me.edgrrrr.de.placeholders.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PAPIEconomyEnabled extends DivinityExpansion {
    public PAPIEconomyEnabled(DEPlugin main) {
        super(main, "^economy_enabled$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            return "uknown";
        }
        return getMain().getWorldMan().isEconomyEnabled(onlinePlayer.getWorld()) ? "Enabled" : "Disabled";
    }
}
