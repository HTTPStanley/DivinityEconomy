package me.edgrrrr.de.placeholders.expansions.market;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.lang.LangEntry;
import me.edgrrrr.de.market.items.enchants.EnchantValueResponse;
import me.edgrrrr.de.market.items.materials.MarketableMaterial;
import me.edgrrrr.de.placeholders.DivinityExpansion;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PAPIHandEnchantValue extends DivinityExpansion {
    public PAPIHandEnchantValue(DEPlugin main) {
        super(main, "^(raw_|)hand_(b|s)value_enchants$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        // Get if raw value
        boolean formatted = value.replaceFirst(this.value, "$1").isEmpty();
        boolean isPurchase = !value.replaceFirst(this.value, "$2").equals("s");

        // Get player
        Player onlinePlayer = player.getPlayer();

        // Check if player is online
        if (onlinePlayer == null)
            return returnEmpty();


        // Get player's hand item
        ItemStack handItem = onlinePlayer.getInventory().getItemInMainHand();
        ItemStack[] handItems = new ItemStack[] {handItem};

        // Check if player is holding an item
        if (handItem.getType() == Material.AIR)
            return returnEmpty();


        // Check if player is holding more than one item
        if (handItem.getAmount() == 0)
            return returnEmpty();


        // Get the total value of the material
        MarketableMaterial marketableMaterial = getMain().getMarkMan().getItem(handItem);

        // Ensure the material is marketable
        if (marketableMaterial == null)
            return returnEmpty();


        // Get material value and enchant value
        EnchantValueResponse enchantResponse;
        if (isPurchase) {
            enchantResponse = getMain().getEnchMan().getBuyValue(handItems);
        } else {
            enchantResponse = getMain().getEnchMan().getSellValue(handItems);
        }


        if (enchantResponse.isFailure())
            return returnEmpty();


        // Return total value
        if (formatted)
            return getMain().getConsole().formatMoney(enchantResponse.getValue());

        return String.format("%,.2f", enchantResponse.getValue());
    }


    public String returnEmpty() {
        return LangEntry.W_empty.get(getMain());
    }
}
