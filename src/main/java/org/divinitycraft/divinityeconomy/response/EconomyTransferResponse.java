package org.divinitycraft.divinityeconomy.response;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Stores the results of a transaction between two players
 */
public class EconomyTransferResponse extends Response {
    // The senders balance
    public final double senderBalance;
    // The receivers balance
    public final double receiverBalance;
    // The amount sent between the players
    public final double amountSent;

    /**
     * Constructor
     *
     * @param senderBalance   - The balance of the sender
     * @param receiverBalance - The balance of the receiver
     * @param amountSent      - The amount sent between the players
     * @param responseType    - The type of response
     * @param errorMessage    - The error message, if any.
     */
    public EconomyTransferResponse(double senderBalance, double receiverBalance, double amountSent, ResponseType responseType, String errorMessage) {
        super(responseType, errorMessage);
        this.senderBalance = senderBalance;
        this.receiverBalance = receiverBalance;
        this.amountSent = amountSent;
    }
}
