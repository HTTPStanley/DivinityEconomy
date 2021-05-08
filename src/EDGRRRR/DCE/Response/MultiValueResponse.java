package edgrrrr.dce.response;

import net.milkbowl.vault.economy.EconomyResponse;

import java.util.HashMap;
import java.util.Set;

/**
 * A multiple value response
 */
public class MultiValueResponse extends Response {
    public final HashMap<String, Double> values;
    public final HashMap<String, Integer> quantities;


    /**
     * Constructor
     * @param values       - A HashMap<String, Double> for storing ItemIds and their individual values.
     * @param quantities   - A HashMap<String, Integer> for storing ItemIds and their individual quantities.
     * @param responseType - The type of response
     * @param errorMessage - The error, if any.
     */
    public MultiValueResponse(HashMap<String, Double> values, HashMap<String, Integer> quantities, EconomyResponse.ResponseType responseType, String errorMessage) {
        super(responseType, errorMessage);
        this.values = values;
        this.quantities = quantities;
    }

    /**
     * Returns the sum of all values
     * @return double
     */
    public double getTotalValue() {
        double totalValue = 0;
        for (double value : this.values.values()) {
            totalValue += value;
        }
        return totalValue;
    }

    /**
     * Returns the sum of all quantities
     * @return int
     */
    public int getTotalQuantity() {
        int totalQuantity = 0;
        for (int quantity : this.quantities.values()) {
            totalQuantity += quantity;
        }
        return totalQuantity;
    }

    /**
     * A method for creating a hashmap with the necessary data types for the values
     * @return HashMap<String, Double>
     */
    public static HashMap<String, Double> createValues() {
        return new HashMap<>();
    }

    /**
     * A method for creating a hashmap with the necessary data types for the quantities
     * @return HashMap<String, Integer>
     */
    public static HashMap<String, Integer> createQuantities() {
        return new HashMap<>();
    }

    /**
     * A method for returning the Set of item id's
     * @return Set<String>
     */
    public Set<String> getItemIds() {
        Set<String> keys1 = values.keySet();
        Set<String> keys2 = quantities.keySet();
        if (!(keys1.containsAll(keys2) && keys2.containsAll(keys1))) {
            return null;
        } else {
            return keys1;
        }
    }

    /**
     * Returns all items with a prefix before them
     * @param prefix - The string to concatenate at the beginning
     * @return String
     */
    public String toString(String prefix) {
        return String.format("%s: %s", prefix, this);
    }

    /**
     * Returns a string containing all item id's within the Quantities alongside their quantity
     * @return String
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int idx = 1;
        for (String enchantID : this.quantities.keySet()) {
            string.append(String.format("%d %s", this.quantities.get(enchantID), enchantID));
            if (idx < this.quantities.keySet().size()) {
                string.append(", ");
                idx += 1;
            }
        }
        return string.toString();
    }
}
