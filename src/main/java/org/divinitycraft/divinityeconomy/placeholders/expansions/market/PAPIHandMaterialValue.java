package org.divinitycraft.divinityeconomy.placeholders.expansions.market;

import org.divinitycraft.divinityeconomy.DEPlugin;
import org.divinitycraft.divinityeconomy.lang.LangEntry;
import org.divinitycraft.divinityeconomy.market.items.materials.MarketableMaterial;
import org.divinitycraft.divinityeconomy.placeholders.DivinityExpansion;
import org.divinitycraft.divinityeconomy.response.ValueResponse;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PAPIHandMaterialValue extends DivinityExpansion {
    public PAPIHandMaterialValue(DEPlugin main) {
        super(main, "^(raw_|)hand_(b|s)value_material$");
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
        ValueResponse materialResponse;
        if (isPurchase)
            materialResponse = marketableMaterial.getManager().getBuyValue(handItem, handItem.getAmount());
        else
            materialResponse = marketableMaterial.getManager().getSellValue(handItem, handItem.getAmount());


        // Return total value
        if (materialResponse.isFailure())
            return returnEmpty();

        // Return total value
        if (formatted)
            return getMain().getConsole().formatMoney(materialResponse.getValue());

        return String.format("%,.2f", materialResponse.getValue());
    }


    public String returnEmpty() {
        return LangEntry.W_empty.get(getMain());
    }
}
