package EDGRRRR.DCE.Economy;

import com.sun.istack.internal.NotNull;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Stores the results of a transaction between two players
 */
public class EconomyTransferResponse {
    // The senders balance
    public final double senderBalance;
    // The receivers balance
    public final double receiverBalance;
    // The amount sent between the players
    public final double amountSent;
    // The type of response
    public final EconomyResponse.ResponseType responseType;
    // The error message
    public final String errorMessage;

    /**
     * Constructor
     * @param senderBalance - The balance of the sender
     * @param receiverBalance - The balance of the receiver
     * @param amountSent - The amount sent between the players
     * @param responseType - The type of response
     * @param errorMessage - The error message, if any.
     */
    public EconomyTransferResponse(double senderBalance, double receiverBalance, double amountSent, EconomyResponse.ResponseType responseType, String errorMessage) {
        this.senderBalance = senderBalance;
        this.receiverBalance = receiverBalance;
        this.amountSent = amountSent;
        this.responseType = responseType;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns if the value was successful or not
     * @return boolean
     */
    @NotNull
    public boolean isSuccess() {
        return this.responseType == EconomyResponse.ResponseType.SUCCESS;
    }

    /**
     * Returns if the value was a failure or not
     * @return boolean
     */
    @NotNull
    public boolean isFailure() {
        return !this.isSuccess();
    }
}
