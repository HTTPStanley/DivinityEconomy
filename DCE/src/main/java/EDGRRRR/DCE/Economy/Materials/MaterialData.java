package EDGRRRR.DCE.Economy.Materials;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MaterialData {
    private boolean isBanned;
    private int quantity;
    private String materialName;
    private Material material;
    private MaterialPotionData potionData;
    private String entityName;


    public MaterialData(
        boolean isBanned,
        int quantity,
        String materialName,
        ConfigurationSection potionData,
        String entityName
    ) {
        this.isBanned = isBanned;
        this.quantity = quantity;
        this.materialName = materialName;
        this.material = Material.getMaterial(materialName);

        // Potiondata
        if (!(potionData == null)) this.potionData = new MaterialPotionData(potionData.getBoolean("extended"), potionData.getString("type"), potionData.getBoolean("upgraded"));

        // Entitydata
        this.entityName = entityName;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public boolean getIsBanned() {
        return this.isBanned;
    }

    public String getMaterialName() {
        return this.materialName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public MaterialPotionData getPotData() {
        return this.potionData;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setQuantity(int amount) {
        this.quantity = amount;
    }

    private void addQuantity(int amount) {
        this.quantity += amount;
    }

    private void remQuantity(int amount) {
        this.quantity -= amount;
    }
}
