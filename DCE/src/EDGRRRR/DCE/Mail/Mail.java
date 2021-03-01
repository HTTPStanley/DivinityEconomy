package EDGRRRR.DCE.Mail;

import EDGRRRR.DCE.Main.DCEPlugin;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.util.Calendar;
import java.util.HashMap;

public class Mail {
    private static final String[] strings = {"<amount>", "<balance>", "<daysAgo>", "<roundedAmount>", "<roundedBalance>", "<sourceUUID>", "<sourceName>"};
    private final MailList mailList;
    private final ConfigurationSection configurationSection;
    private final HashMap<String, String> stringReplacementMap;
    private final String[] resultingStrings;

    /**
     * Constructor
     *
     * @param configurationSection
     * @param mailList
     */
    public Mail(MailList mailList, ConfigurationSection configurationSection) {
        this.mailList = mailList;
        this.configurationSection = configurationSection;
        this.stringReplacementMap = new HashMap<>();
        this.resultingStrings = new String[]{
                String.valueOf(this.getAmount()),
                String.valueOf(this.getNewBalance()),
                String.valueOf(this.getDaysSince()),
                String.valueOf(this.getRoundedAmount()),
                String.valueOf(this.getRoundedNewBalance()),
                this.getSourceUUID(),
                this.getSourceName()
        };

        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            String resultingString = this.resultingStrings[i];
            this.stringReplacementMap.put(string, resultingString);
        }
    }

    public String getSourceName() {
        return DCEPlugin.getApp().getPlayerManager().getOfflinePlayerByUUID(this.getSourceUUID(), true).getName();
    }

    public double getRoundedAmount() {
        return DCEPlugin.getApp().getEconomyManager().round(this.getAmount());
    }

    public double getRoundedNewBalance() {
        return DCEPlugin.getApp().getEconomyManager().round(this.getNewBalance());
    }

    public double getRoundedOldBalance() {
        return DCEPlugin.getApp().getEconomyManager().round(this.getOldBalance());
    }

    /**
     * Returns the Unique ID of the mail
     *
     * @return String - Unique ID of mail
     */
    public String getID() {
        return String.valueOf(this.getDateFrom().getTimeInMillis());
    }

    /**
     * Returns the message of this mail
     *
     * @return String - The message of the mail
     */
    public String getRawMessage() {
        return this.configurationSection.getString(this.mailList.strMessage);
    }

    public String getMessage() {
        String message = this.getRawMessage();
        for (String string : this.stringReplacementMap.keySet()) {
            if (message.contains(string)) {
                message = message.replace(string, this.stringReplacementMap.get(string));
            }
        }
        return message;
    }

    /**
     * Returns the date from when the mail was created
     *
     * @return Calendar - The creation date of the mail
     */
    public Calendar getDateFrom() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(this.configurationSection.getLong(this.mailList.strDate));
        return date;
    }

    /**
     * Returns the time delta between the creation date and now
     *
     * @return Duration - The time delta
     */
    public Duration getTimeSince() {
        Calendar currentDate = Calendar.getInstance();
        return Duration.between(this.getDateFrom().toInstant(), currentDate.toInstant());
    }

    /**
     * Returns the number of days between the creation date and now
     *
     * @return int - The number of days
     */
    public int getDaysSince() {
        return (int) this.getTimeSince().toDays();
    }

    /**
     * Returns the amount of cash transferred, can be negative
     *
     * @return double - The cash amount transferred
     */
    public double getAmount() {
        return this.configurationSection.getDouble(this.mailList.strAmount);
    }

    /**
     * Returns the new balance of the mail owner after the transaction
     *
     * @return double - New balance of user
     */
    public double getNewBalance() {
        return this.configurationSection.getDouble(this.mailList.strBalance);
    }

    /**
     * Returns the old (balance before transaction) balance of the mail owner
     *
     * @return double - The old balance of the user
     */
    public double getOldBalance() {
        return this.getNewBalance() - this.getAmount();
    }

    /**
     * Returns the source or destination of where the cash came from or went to
     *
     * @return OfflinePlayer - The player
     */
    public String getSourceUUID() {
        return (this.configurationSection.getString(this.mailList.strSource));
    }

    /**
     * Returns if this mail has been read or not
     *
     * @return boolean - is read
     */
    public boolean getRead() {
        return this.configurationSection.getBoolean(this.mailList.strRead);
    }

    /**
     * Sets the read state of the mail
     *
     * @param state - The read state
     */
    public void setRead(boolean state) {
        this.configurationSection.set(this.mailList.strRead, state);
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }
}
