package me.edgrrrr.de.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.edgrrrr.de.DEPlugin;
import me.edgrrrr.de.placeholders.expansions.*;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Set;

public class ExpansionManager extends PlaceholderExpansion {
    private final DEPlugin main;
    private final Set<DivinityExpansion> expansions;

    public ExpansionManager(DEPlugin main) {
        this.main = main;
        this.expansions = new HashSet<>();
        // Expansions
        this.expansions.add(new PAPIMaterialInflation(this.main));
        this.expansions.add(new PAPIMaterialTotalDefaultQuantity(this.main));
        this.expansions.add(new PAPIMaterialTotalQuantity(this.main));
        this.expansions.add(new PAPIEnchantInflation(this.main));
        this.expansions.add(new PAPIEnchantTotalDefaultQuantity(this.main));
        this.expansions.add(new PAPIEnchantTotalQuantity(this.main));
        this.expansions.add(new PAPIPlayerBalance(this.main));
        this.expansions.add(new PAPIRawPlayerBalance(this.main));
        this.expansions.add(new PAPIFormatMoney(this.main));
        this.expansions.add(new PAPIRawBalanceMath(this.main));
        this.expansions.add(new PAPIBalanceMath(this.main));
        this.expansions.add(new PAPIRawMaterialBValue(this.main));
        this.expansions.add(new PAPIRawMaterialSValue(this.main));
        this.expansions.add(new PAPIMaterialBValue(this.main));
        this.expansions.add(new PAPIMaterialSValue(this.main));
        this.expansions.add(new PAPIEnchantStock(this.main));
        this.expansions.add(new PAPIMaterialStock(this.main));
        this.expansions.add(new PAPIMaterialStack(this.main));
    }

    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return this.getMain().getDescription().getAuthors().get(0);
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "de";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return this.getMain().getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player     A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param identifier A String containing the identifier/value.
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier != null) {
            // Fill bracket vars
            identifier = PlaceholderAPI.setBracketPlaceholders(player, identifier.toLowerCase());

            for (DivinityExpansion expansion : this.expansions) {
                if (expansion.checkValue(identifier)) {
                    return expansion.getResult(player, identifier);
                }
            }
        }

        // We return null if an invalid placeholder
        // was provided
        return null;
    }

    // Returns the main class
    private DEPlugin getMain() {
        return this.main;
    }

    public int getExpansionCount() {
        return this.expansions.size();
    }
}
