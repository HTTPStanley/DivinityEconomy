package me.edgrrrr.de.placeholderAPI.expansions;

import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.materials.MaterialData;
import me.edgrrrr.de.math.Math;
import me.edgrrrr.de.placeholderAPI.DivinityExpansion;
import me.edgrrrr.de.player.PlayerInventoryManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class materialSValue extends DivinityExpansion {
    public materialSValue(DEPlugin main) {
        super(main, "^material_svalue_(.*)_([0-9]*)$");
    }

    @Override
    public String getResult(OfflinePlayer player, String value) {
        String material = value.replaceFirst(this.value, "$1");
        int amount = Math.getInt(value.replaceFirst(this.value, "$2"));
        MaterialData materialData = this.getMain().getMaterialManager().getMaterial(material);
        if (materialData != null) return this.getMain().getConsole().formatMoney(this.getMain().getMaterialManager().getSellValue(PlayerInventoryManager.createItemStacks(materialData.getMaterial(), amount)).value);
        else return String.format("Unknown material '%s'", material);
    }
}
