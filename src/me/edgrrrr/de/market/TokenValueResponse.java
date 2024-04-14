package me.edgrrrr.de.market;

import me.edgrrrr.de.response.ValueResponse;
import net.milkbowl.vault.economy.EconomyResponse;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenValueResponse extends ValueResponse {
    Map<MarketableToken, Integer> quantities = new ConcurrentHashMap<>();
    Map<MarketableToken, Double> values = new ConcurrentHashMap<>();
    Map<String, MarketableToken> tokenIdMap = new ConcurrentHashMap<>();
    int totalQuantity = 0;

    public TokenValueResponse() {
        super();
    }


    public TokenValueResponse(EconomyResponse.ResponseType type, String message) {
        super(type, message);
    }


    /**
     * Adds a token to the response
     * @param token
     * @param quantity
     * @param value
     * @return
     */
    protected TokenValueResponse addToken(MarketableToken token, int quantity, double value) {
        this.tokenIdMap.put(token.getID(), token);
        return this.addQuantity(token, quantity)
                .addValue(token, value);
    }


    /**
     * Merges this response with another
     * @param response
     * @return
     */
    public TokenValueResponse addResponse(TokenValueResponse response) {
        // Add all tokens
        for (MarketableToken token : response.getTokens()) {
            this.addToken(token, response.getQuantity(token), response.getValue(token));
        }

        // If response is a failure, set this response as a failure
        if (response.isFailure()) {
            this.setFailure(response.getErrorMessage());
        }

        // Return this response
        return this;
    }


    /**
     * Returns the total quantity of all tokens
     * @return
     */
    public int getQuantity() {
        return totalQuantity;
    }


    /**
     * Returns the quantity map
     * @return
     */
    public Map<MarketableToken, Integer> getQuantities() {
        return quantities;
    }


    /**
     * Returns the quantity of the token
     */
    public int getQuantity(@Nonnull MarketableToken token) {
        return quantities.getOrDefault(token, 0);
    }


    /**
     * Returns the quantity of the token, by ID
     */
    public int getQuantity(@Nonnull String id) {
        MarketableToken token = this.getTokenById(id);
        if (token == null) {
            return 0;
        }

        return this.getQuantity(token);
    }


    /**
     * Returns the value map
     * @return
     */
    public Map<MarketableToken, Double> getValues() {
        return values;
    }


    /**
     * Returns the value of the token
     */
    public double getValue(@Nonnull MarketableToken token) {
        return values.getOrDefault(token, 0D);
    }


    /**
     * Returns the value of the token, by ID
     */
    public double getValue(@Nonnull String id) {
        MarketableToken token = this.getTokenById(id);
        if (token == null) {
            return 0D;
        }

        return this.getValue(token);
    }


    /**
     * Returns the token by id
     * @param id
     * @return
     */
    public MarketableToken getTokenById(String id) {
        return tokenIdMap.getOrDefault(id, null);
    }


    /**
     * Returns if the token is in the response
     * @param id
     * @return
     */
    public boolean hasToken(String id) {
        return tokenIdMap.containsKey(id);
    }


    /**
     * Returns if the token is in the response
     * @param token
     * @return
     */
    public boolean hasToken(MarketableToken token) {
        return tokenIdMap.containsValue(token);
    }


    /**
     * Returns the tokens in the response
     * @return
     */
    public List<MarketableToken> getTokens() {
        return new ArrayList<>(tokenIdMap.values());
    }


    /**
     * Returns a list of token ids in the response
     * @return
     */
    public List<String> getTokenIds() {
        return new ArrayList<>(tokenIdMap.keySet());
    }


    /**
     * Add a quantity to the quantity of the token
     * @param token
     * @param quantity
     */
    private TokenValueResponse addQuantity(MarketableToken token, int quantity) {
        quantities.put(token, quantities.getOrDefault(token, 0) + quantity);
        this.totalQuantity += quantity;
        return this;
    }


    /**
     * Add a value to the value of the token
     * @param token
     * @param value
     */
    private TokenValueResponse addValue(MarketableToken token, double value) {
        values.put(token, values.getOrDefault(token, 0D) + value);
        this.addValue(value);
        return this;
    }


    /**
     * Return all names
     */
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (MarketableToken token : this.getTokens()) {
            names.add(token.getName());
        }

        return names;
    }


    /**
     * List names
     */
    public String listNames() {
        return String.join(", ", this.getNames());
    }


    /**
     * List names with prefix
     */
    public String listNames(String prefix) {
        return String.format("%s %s", prefix, String.join(", ", this.getNames()));
    }


    /**
     * Returns the toString of the response
     */
    @Override
    public String toString() {
        return String.format("TokenValueResponse{ type=%s, message=%s, quantity=%d, value=%f }",
                this.getResponseType(),
                this.getErrorMessage(),
                this.getQuantity(),
                this.getValue());
    }
}
