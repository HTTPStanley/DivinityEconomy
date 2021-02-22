package EDGRRRR.DCE.Mail;

import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.util.Calendar;

public class Mail {
    // Stores the mail message
    private final String message;
    // Stores when the mail was created
    private final Calendar dateFrom;
    // Stores the amount of cash transferred
    private final double amount;
    // Stores the new balance of the user
    private final double newBalance;
    // Stores the source of the cash (could also be where the cash went)
    private final OfflinePlayer source;
    // Stores whether this mail has been read or not
    private boolean read;

    /**
     * Constructor
     * @param message - The mail message
     * @param dateFrom - The date when the mail was created
     * @param amount - The amount transferred
     * @param newBalance - The new balance of the user
     * @param source - The source or direction the cash came from/went to
     * @param read - Whether this mail has been read or not
     */
    public Mail(String message, Calendar dateFrom, double amount, double newBalance, OfflinePlayer source, boolean read) {
        this.message = message;
        this.dateFrom = dateFrom;
        this.amount = amount;
        this.newBalance = newBalance;
        this.source = source;
        this.read = read;
    }

    /**
     * Returns the Unique ID of the mail
     * @return String - Unique ID of mail
     */
    public String getID() {
        return String.valueOf(this.dateFrom.getTimeInMillis());
    }

    /**
     * Returns the message of this mail
     * @return String - The message of the mail
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the date from when the mail was created
     * @return Calendar - The creation date of the mail
     */
    public Calendar getDateFrom() {
        return this.dateFrom;
    }

    /**
     * Returns the time delta between the creation date and now
     * @return Duration - The time delta
     */
    public Duration getTimeSince() {
        Calendar currentDate = Calendar.getInstance();
        return Duration.between(this.dateFrom.toInstant(), currentDate.toInstant());
    }

    /**
     * Returns the number of days between the creation date and now
     * @return int - The number of days
     */
    public int getDaysSince() {
        return (int) this.getTimeSince().toDays();
    }

    /**
     * Returns the amount of cash transferred, can be negative
     * @return double - The cash amount transferred
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Returns the new balance of the mail owner after the transaction
     * @return double - New balance of user
     */
    public double getNewBalance() {
        return newBalance;
    }

    /**
     * Returns the old (balance before transaction) balance of the mail owner
     * @return double - The old balance of the user
     */
    public double getOldBalance() {
        return this.newBalance - this.amount;
    }

    /**
     * Returns the source or destination of where the cash came from or went to
     * @return OfflinePlayer - The player
     */
    public OfflinePlayer getSource() {
        return this.source;
    }

    /**
     * Returns if this mail has been read or not
     * @return boolean - is read
     */
    public boolean getRead() {
        return this.read;
    }

    /**
     * Sets the read state of the mail
     * @param state - The read state
     */
    public void setRead(boolean state) {
        this.read = state;
    }
}
