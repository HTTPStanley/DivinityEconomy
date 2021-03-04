package EDGRRRR.DCE.Economy;

import net.milkbowl.vault.economy.EconomyResponse;

public class EconomyTransferResponse {
    public final double senderBalance;
    public final double receiverBalance;
    public final double amountSent;
    public final EconomyResponse.ResponseType responseType;
    public final String errorMessage;

    public EconomyTransferResponse(double senderBalance, double receiverBalance, double amountSent, EconomyResponse.ResponseType responseType, String errorMessage) {
        this.senderBalance = senderBalance;
        this.receiverBalance = receiverBalance;
        this.amountSent = amountSent;
        this.responseType = responseType;
        this.errorMessage = errorMessage;
    }
}
