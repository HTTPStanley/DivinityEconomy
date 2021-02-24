package EDGRRRR.DCE.Mail;

import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.util.Calendar;

public class Mail {
    private final MailList mailList;
    private final ConfigurationSection configurationSection;

    /**
     * Constructor
     * @param configurationSection
     * @param mailList
     */
    public Mail(MailList mailList, ConfigurationSection configurationSection) {
        this.mailList = mailList;
        this.configurationSection = configurationSection;
    }

    /**
     * Returns the Unique ID of the mail
     * @return String - Unique ID of mail
     */
    public String getID() {
        return String.valueOf(this.getDateFrom().getTimeInMillis());
    }

    /**
     * Returns the message of this mail
     * @return String - The message of the mail
     */
    public String getMessage() {
        return this.configurationSection.getString(this.mailList.strMessage);
    }

    /**
     * Returns the date from when the mail was created
     * @return Calendar - The creation date of the mail
     */
    public Calendar getDateFrom() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.configurationSection.getLong(this.mailList.strDate));
        return date;
    }

    /**
     * Returns the time delta between the creation date and now
     * @return Duration - The time delta
     */
    public Duration getTimeSince() {
        Calendar currentDate = Calendar.getInstance();
        return Duration.between(this.getDateFrom().toInstant(), currentDate.toInstant());
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
        return this.configurationSection.getDouble(this.mailList.strAmount);
    }

    /**
     * Returns the new balance of the mail owner after the transaction
     * @return double - New balance of user
     */
    public double getNewBalance() {
        return this.configurationSection.getDouble(this.mailList.strBalance);
    }

    /**
     * Returns the old (balance before transaction) balance of the mail owner
     * @return double - The old balance of the user
     */
    public double getOldBalance() {
        return this.getNewBalance() - this.getAmount();
    }

    /**
     * Returns the source or destination of where the cash came from or went to
     * @return OfflinePlayer - The player
     */
    public String getSourceUUID() {
        return (this.configurationSection.getString(this.mailList.strSource));
    }

    /**
     * Returns if this mail has been read or not
     * @return boolean - is read
     */
    public boolean getRead() {
        return this.configurationSection.getBoolean(this.mailList.strRead);
    }

    /**
     * Sets the read state of the mail
     * @param state - The read state
     */
    public void setRead(boolean state) {
        this.configurationSection.set(this.mailList.strRead, state);
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }
}
