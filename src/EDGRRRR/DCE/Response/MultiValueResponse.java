package EDGRRRR.DCE.Response;

import net.milkbowl.vault.economy.EconomyResponse;

import java.util.HashMap;
import java.util.Set;

/**
 * A multiple value response
 */
public class MultiValueResponse extends ValueResponse {
    public final HashMap<String, Double> values;
    public final HashMap<String, Integer> quantities;


    /**
     * Constructor
     * @param totalValue   - The total value of the enchant
     * @param values       - A HashMap<String, Double> for storing ItemIds and their individual values.
     * @param quantities   - A HashMap<String, Integer> for storing ItemIds and their individual quantities.
     * @param responseType - The type of response
     * @param errorMessage - The error, if any.
     */
    public MultiValueResponse(double totalValue, HashMap<String, Double> values, HashMap<String, Integer> quantities, EconomyResponse.ResponseType responseType, String errorMessage) {
        super(totalValue, responseType, errorMessage);
        this.values = values;
        this.quantities = quantities;
    }

    public static HashMap<String, Double> createValues() {
        return new HashMap<String, Double>();
    }

    public static HashMap<String, Integer> createQuantities() {
        return new HashMap<String, Integer>();
    }

    public Set<String> getItemIds() {
        Set<String> keys1 = values.keySet();
        Set<String> keys2 = quantities.keySet();
        if (!(keys1.containsAll(keys2) && keys2.containsAll(keys1))) {
            return null;
        } else {
            return keys1;
        }
    }
}
